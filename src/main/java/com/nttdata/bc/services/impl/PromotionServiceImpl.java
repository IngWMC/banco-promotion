package com.nttdata.bc.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.nttdata.bc.exceptions.NotFoundException;
import com.nttdata.bc.models.Promotion;
import com.nttdata.bc.services.IPromotionService;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Multi;
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
    public Multi<Promotion> listAll() {
        return this.reactiveRedisDataSource.key().keys(patternKey + "*")
                .onItem()
                .transformToMulti(keys -> Multi.createFrom().iterable(keys))
                .flatMap(key -> {
                    return this.reactiveRedisDataSource
                            .value(String.class, Promotion.class)
                            .get(key).toMulti();
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
