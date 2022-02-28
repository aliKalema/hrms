package com.smartmax.hrms.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class AdditionalPaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
    private Set<ExtraPayhead> extraPayheads;
    @OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
    private Set<Relief> reliefs;
    public AdditionalPaymentDetails(int id, Set<ExtraPayhead> extraPayheads, Set<Relief> reliefs) {
        this.id = id;
        this.extraPayheads = extraPayheads;
        this.reliefs = reliefs;
    }
    public AdditionalPaymentDetails(){
        super();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Set<ExtraPayhead> getExtraPayheads() {
        return extraPayheads;
    }
    public void setExtraPayheads(Set<ExtraPayhead> extraPayheads) {
        this.extraPayheads = extraPayheads;
    }
    public Set<Relief> getReliefs() {
        return reliefs;
    }
    public void setReliefs(Set<Relief> reliefs) {
        this.reliefs = reliefs;
    }

    @Override
    public String toString() {
        return "AdditionalPaymentDetails{" +
                "id=" + id +
                ", extraPayheads=" + extraPayheads.size() +
                ", reliefs=" + reliefs.size() +
                '}';
    }
}
