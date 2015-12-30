/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diceDB.service;

import com.diceDB.Scores;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author Tim
 */
@Stateless
@Path("com.dicedb.scores")
public class ScoresFacadeREST extends AbstractFacade<Scores> {
    @PersistenceContext(unitName = "DiceServerPU")
    private EntityManager em;

    public ScoresFacadeREST() {
        super(Scores.class);
    }
    
    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public Response createCustom(Scores entity) {
        System.out.println("Hier is ie");
        // Initialize response
        Response response;
        // Create query wich searches for a excisting score of an androidId and location.
        Query q = em.createQuery("SELECT s.value FROM Scores s WHERE s.androidId = :androidId AND s.location = :location");
        // Set paramaters from JSON post.
        q.setParameter("androidId", entity.getAndroidId());
        q.setParameter("location", entity.getLocation());
        // Set boolean to check if there is a result.
        boolean result = (q.getResultList().isEmpty());
        if (result) {
            // No excisting score is found, create a new score.
            System.out.println("Nothing found, create new score.");
            super.create(entity);
            // Set response
            response = Response.status(Response.Status.CREATED).build();
        } else {
            // There is a score with user and location.
            // Convert object (the old score) to int and set variable from database.
            int oldValue = (int) q.getSingleResult();
            // Set variable newValue from entity.
            int newValue = entity.getValue();
            // Print new value and androidId.
            System.out.println("A new score is posted: " + newValue + " for androidId: "
                    + entity.getAndroidId());
            // Check if the new score is higher than the one in the database.
            if (newValue > oldValue) {
                // New score is higher.
                System.out.println("The new score: " + newValue + ", is higher than "
                        + "the one in the database: " + oldValue);
                // Update score.
                super.edit(entity);
                System.out.println("Database is update on location " + entity.getLocation()
                        + " with score " + newValue + " for "
                        + "androidId " + entity.getAndroidId());
                // Set response
                response = Response.status(Response.Status.CREATED).build();
            } else {
                // New score is lower or same.
                System.out.println("Nothing happens, new score is lower or same as "
                        + "score in database.");
                // Set response
                response = Response.status(Response.Status.OK).build();
            }
        }
        // Return the response code
        System.out.println("Return message: " + response);
        return response;
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Scores entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

        @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Scores find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    
//    @GET
//    @Path("{androidId}")
//    @Produces({"application/xml", "application/json"})
//    public Scores find(@PathParam("androidId") String androidId) {
//        Query q = em.createQuery("SELECT * FROM SCORES WHERE ANDROID_ID = "
//                + "CHAR(123) ORDER BY VALUE DESC");
//        // Derby ondersteunt geen LIMIT functie!!!
//        // Zelf iets omheen bouwen.
//        q.setParameter("androidId", androidId);
//        List<Scores> results = q.getResultList();
////        return results;
//    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Scores> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Scores> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
