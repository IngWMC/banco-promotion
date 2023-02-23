package com.nttdata.bc.services;

import java.util.List;

import com.nttdata.bc.models.Promotion;

import io.smallrye.mutiny.Uni;

public interface IPromotionService {
    Uni<Promotion> insert(Promotion obj);

    Uni<Promotion> update(Promotion obj);

    Uni<List<Promotion>> listAll();

    Uni<Promotion> findById(String id);

    Uni<Void> deleteById(String id);
}
