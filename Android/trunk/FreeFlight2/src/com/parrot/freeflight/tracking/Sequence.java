package com.parrot.freeflight.tracking;

import java.util.ArrayList;

import android.util.Pair;



public class Sequence {
	private ArrayList<Pair<SequenceType, Integer> > sequences;
	
	Sequence(){
		sequences = new ArrayList< Pair<SequenceType, Integer> >();
	}
	
	public void AddAction(SequenceType action, int afterTime){
		Pair<SequenceType, Integer> pair = Pair.create(action, afterTime);
		sequences.add(pair);
	}
	
	public int Length(){
		return sequences.size();
	}
	
	public Pair<SequenceType, Integer> get(int index){
		return sequences.get(index);
	}
}
