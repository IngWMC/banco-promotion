package com.nttdata.bc.services;

import com.nttdata.bc.models.Promotion;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface IPromotionService {
    Uni<Promotion> insert(Promotion obj);

    Uni<Promotion> update(Promotion obj);

    Multi<Promotion> listAll();

    Uni<Promotion> findById(String id);

    Uni<Void> deleteById(String id);
}
