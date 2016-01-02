/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diceDB.service;

import com.diceDB.Scores;
import com.diceDB.Users;
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
    @Consumes({"application/json"})
    public Response createCustom(Scores entity) {
        System.out.println("Hier is ie");
        // Initialize response
        Response response;
        // Create query wich searches for a excisting score of an androidId and location.
        Query q = em.createQuery("SELECT s.value FROM Scores s WHERE s.androidId = :androidId AND s.location = :location ORDER BY s.value DESC");
        // Set paramaters from JSON post.
        q.setParameter("androidId", entity.getAndroidId());
        q.setParameter("location", entity.getLocation());
        // Set maximum results to 1.
        q.setMaxResults(1);
        // Debugging
        System.out.println("Query: " + q);
        System.out.println("Entity androidId: " + entity.getAndroidId());
        System.out.println("Entity location: " + entity.getLocation());
        System.out.println("Query resultlist: " + q.getResultList());
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
            int oldValue = (int) q.getResultList().get(0);
            System.out.println("oldValue: " + oldValue);
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
                // Build query to search for scoreId
                Query r = em.createQuery("SELECT s.scoreId FROM Scores s WHERE s.androidId = :androidId AND s.location = :location ORDER BY s.value DESC");
                // Set paramaters from JSON post.
                r.setParameter("androidId", entity.getAndroidId());
                r.setParameter("location", entity.getLocation());
                // Get the scoreId and set in variable.
                int oldScoreId = (int) r.getResultList().get(0);
                System.out.println("oldScoreId: " + oldScoreId);
                // Set scoreId in entity for updating the proper row.
                entity.setScoreId(oldScoreId);
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
    @Consumes({"application/json"})
    public void edit(Scores entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }
    
    @GET
    @Path("/androidId/{androidId}")
    @Produces({"application/json"})
    public List<Scores> findByAndroidId(@PathParam("androidId") Users androidId) {
        System.out.println("Doing a get of 10 highest scores for androidId: " 
                + androidId);
        // Build a query to select the 10 highest scores and their corresponding 
        // locations for the submitted androidId.
        Query q = em.createQuery("SELECT s.value, s.location FROM Scores s WHERE s.androidId = :androidId ORDER BY s.value DESC", Scores.class);
        // Set maximum results to 10.
        q.setMaxResults(10);
        // Set paramater.
        q.setParameter("androidId", androidId);
        // Debug
        System.out.println("Query: " + q);
        // Set the list with scores in variable.
        System.out.println("Paramters: " + q.getParameters());
        List<Scores> results = q.getResultList();
        // Return results.
        
//        for(int i = 0; i < results.size(); i++){
//            System.out.println("Testing: " + results.get(i));
//        }
        
        System.out.println("Results: " + results.toString());
        return results;
    }
    
    @GET
    @Path("/location/{location}")
    @Produces({"application/json"})
    public List<Scores> findByLocation(@PathParam("location") String location) {
        System.out.println("Doing a get of 10 highest scores for location: " 
                + location);
        // Build a query to select 10 highest scores and corresponding names 
        // for the submitted location.
        Query q = em.createQuery("SELECT s.value, s.androidId.name FROM Scores s WHERE s.location = :location ORDER BY s.value DESC", Scores.class);
        // Set maximum results to 10.
        q.setMaxResults(10);
        // Set parameter.
        q.setParameter("location", location);
        // Debug
        System.out.println("Query: " + q);
        // Set the list with scores in variable.
        List<Scores> results = q.getResultList();
        // Return results.
        System.out.println("Results: " + results);
        return results;
    }
    
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Scores find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Scores> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
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