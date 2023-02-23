package com.nttdata.bc.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.nttdata.bc.exceptions.NotFoundException;
import com.nttdata.bc.models.Promotion;
import com.nttdata.bc.services.IPromotionService;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PromotionServiceImpl implements IPromotionService {
    @Inject
    Logger LOGGER;

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    @ConfigProperty(name = "pattern-key")
    String patternKey;

    @Override
    public Uni<Promotion> insert(Promotion obj) {
        String id = this.getUuid();
        String key = this.getKey(id);
        obj.setId(id);
        obj.setCreatedAt(LocalDateTime.now());

        return this.reactiveRedisDataSource
                .value(String.class, Promotion.class)
                .set(key, obj)
                .flatMap(r -> {
                    return this.reactiveRedisDataSource
                            .value(String.class, Promotion.class)
                            .get(key);
                });
    }

    @Override
    public Uni<Promotion> update(Promotion obj) {
        String key = this.getKey(obj.getId());
        obj.setUpdateddAt(LocalDateTime.now());
        LOGGER.info("update ::: obj ::: " + obj);
        return this.reactiveRedisDataSource
                .value(String.class, Promotion.class)
                .get(key).onItem()
                .ifNull()
                .failWith(() -> new NotFoundException("La promoción con el id: " + obj.getId() + ", no existe."))
                .flatMap((r) -> this.reactiveRedisDataSource
                        .value(String.class, Promotion.class)
                        .set(key, obj)
                        .flatMap((v) -> {
                            return this.reactiveRedisDataSource
                                    .value(String.class, Promotion.class)
                                    .get(key);
                        }));
    }

    @Override
    public Uni<List<Promotion>> listAll() {
        return this.reactiveRedisDataSource.key().keys(patternKey + "*")
                .onItem().ifNotNull()
                .transformToUni(keys -> {
                    LOGGER.info("listAll ::: keys ::: " + keys);
                    List<Promotion> promotions = new ArrayList<>();

                    promotions = keys.stream().map(key -> {
                        LOGGER.info("stream ::: keys ::: " + key);
                        Promotion promotion = this.reactiveRedisDataSource
                                .value(String.class, Promotion.class)
                                .get(key)
                                .await().indefinitely();
                        return promotion;
                    }).collect(Collectors.toList());

                    return Uni.createFrom().item(promotions);
                });
    }

    @Override
    public Uni<Promotion> findById(String id) {
        String key = this.getKey(id);
        return this.reactiveRedisDataSource
                .value(String.class, Promotion.class)
                .get(key).onItem()
                .ifNull()
                .failWith(() -> new NotFoundException("La promoción con el id: " + id.toString() + ", no existe."));
    }

    @Override
    public Uni<Void> deleteById(String id) {
        String key = this.getKey(id);

        return this.reactiveRedisDataSource
                .value(String.class, Promotion.class)
                .get(key).onItem()
                .ifNull()
                .failWith(() -> new NotFoundException("La promoción con el id: " + id.toString() + ", no existe."))
                .flatMap((r) -> this.reactiveRedisDataSource
                        .value(String.class, Promotion.class)
                        .getdel(key)
                        .replaceWithVoid());
    }

    private String getUuid() {
        UUID uuid = UUID.randomUUID();
        LOGGER.info("getUuid ::: uuid ::: " + uuid.toString());
        return uuid.toString();
    }

    private String getKey(String id) {
        return patternKey + id;
    }
}
