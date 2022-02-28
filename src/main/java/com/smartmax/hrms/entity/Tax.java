package com.smartmax.hrms.entity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
@Entity
public class Tax {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private double less;
	private double overBand;
	private double overRate;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	@JoinColumn(name="tax_id")
	private List<Ranges>next;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	@JoinColumn(name="tax_id")
	private Set<Relief>reliefs;
	@Transient
	private double grossTax;
	@Transient
	private double netTax;
	public Tax(int id, double less, double overBand, double overRate, List<Ranges> next, Set<Relief> relefs) {
		super();
		this.id = id;
		this.less = less;
		this.overBand = overBand;
		this.overRate = overRate;
		this.next = next;
		this.reliefs = relefs;
	}
	public Tax() {
		super();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLess() {
		return less;
	}
	public void setLess(double less) {
		this.less = less;
	}
	public double getOverBand() {
		return overBand;
	}
	public void setOverBand(double overBand) {
		this.overBand = overBand;
	}
	public double getOverRate() {
		return overRate;
	}
	public void setOverRate(double overRate) {
		this.overRate = overRate;
	}
	public List<Ranges> getNext() {
		return next;
	}
	public void setNext(List<Ranges> next) {
		this.next = next;
	}
	public Set<Relief> getReliefs() {
		return reliefs;
	}
	public void setReliefs(Set<Relief> reliefs) {
		this.reliefs = reliefs;
	}
	public double calculate(double taxableAmount) {
		double initialAmount = taxableAmount;
		double tax = 0.00;
		if(taxableAmount<less) {
			System.out.println("LESS THAN : "+less+" EXEMPTED");
			return tax;
		}
		Map<Integer,Ranges>nextMap= new HashMap<>();
		for(Ranges range:this.next){
			nextMap.put(range.getPosition(),range);
		}
		for(int i=2;i<nextMap.size()+2;i++){
			System.out.println("_-_-_-_-_-_-_-_-_-_-_");
			Ranges range = nextMap.get(i);
			System.out.println("Taxable Amount "+taxableAmount);
			System.out.println((i-1)+" BAND : "+" : "+range.getBand());
			double difference = taxableAmount - range.getBand();
			System.out.println("Difference "+difference);
			if(difference>=0){
				double bandTax = range.getBand() * ((range.getRate())/100);
				taxableAmount = taxableAmount - Math.round(range.getBand()* 100.0) / 100.0;
				tax = tax + bandTax;
				System.out.println("Band Tax : "+bandTax);
				System.out.println("Total Tax : "+tax);
				System.out.println("Taxable Amount " + taxableAmount);
			}
			else{
				double bandTax = taxableAmount * Math.round(((range.getRate())/100)* 100.0) / 100.0;
				tax = tax + bandTax;
				taxableAmount =0;
				System.out.println("Band Tax : "+bandTax);
				System.out.println("Total Tax : "+tax);
				System.out.println("Taxable Amount " + taxableAmount);
				return tax;
			}
		}
		if(initialAmount > overBand) {
			System.out.println((nextMap.size()+1)+" BAND : "+" : "+this.overRate);
			double overBand = taxableAmount * Math.round((overRate/100)* 100.0) / 100.0;
			tax = tax + overBand;
			System.out.println("Over Tax "+overBand);
			System.out.println("Total Tax "+tax);
		}
		return tax;
	}
	
	public double getGrossTax() {
		return grossTax;
	}
	@Override
	public String toString() {
		String tax = "";
		tax =tax + String.valueOf(less)+"\n";
		tax = tax + String.valueOf(overBand) +" : "+ String.valueOf(overRate) +"\n";
		for(int i =0;i<next.size();i++){
			tax= tax +next.get(i).toString() +"\n";
		}
		return tax;
	}
	
}
