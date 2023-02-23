package com.nttdata.bc.resources;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;

import com.nttdata.bc.models.Promotion;
import com.nttdata.bc.services.IPromotionService;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/promotions")
public class PromotionResource {
    @Inject
    Logger LOGGER;

    @Inject
    IPromotionService service;

    @POST
    @ResponseStatus(StatusCode.OK)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Promotion> insert(@Valid Promotion obj) {
        return this.service.insert(obj);
    }

    @PUT
    @Path("/{id}")
    @ResponseStatus(StatusCode.CREATED)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Promotion> update(@PathParam("id") String id, @Valid Promotion obj) {
        obj.setId(id);
        return this.service.update(obj);
    }

    @GET
    @Path("/")
    @ResponseStatus(StatusCode.OK)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Promotion> listAll() {
        return this.service.listAll();
    }

    @GET
    @Path("/{id}")
    @ResponseStatus(StatusCode.OK)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Promotion> findById(@PathParam("id") String id) {
        return this.service.findById(id);
    }

    @DELETE
    @Path("/{id}")
    @ResponseStatus(StatusCode.NO_CONTENT)
    public Uni<Void> deleteById(@PathParam("id") String id) {
        return this.service.deleteById(id);
    }
}
