package com.nathantonani.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Trailers {

    private Integer id;
    private List<Object> quicktime = new ArrayList<Object>();

    @SerializedName("youtube")
    private List<Trailer> trailers = new ArrayList<Trailer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The quicktime
     */
    public List<Object> getQuicktime() {
        return quicktime;
    }

    /**
     *
     * @param quicktime
     * The quicktime
     */
    public void setQuicktime(List<Object> quicktime) {
        this.quicktime = quicktime;
    }

    /**
     *
     * @return
     * The trailer
     */
    public List<Trailer> getTrailers() {
        return trailers;
    }

    /**
     *
     * @param trailer
     * The trailer
     */
    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}