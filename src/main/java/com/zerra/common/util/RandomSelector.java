package com.zerra.common.util;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

public class RandomSelector<T> {

	private boolean isWeighted;
	private final Random random;
	private final HashMap<T, Double> weights;
	
	public RandomSelector(Random random) {
		this.random = random;
		this.weights = new HashMap<>();
	}
	
	public RandomSelector() {
		this(new SecureRandom());
	}
	
	public void addEntry(T object, double rarity) {
		weights.put(object, rarity);
	}
	
	public void setWeighted(boolean weighted) {
		this.isWeighted = weighted;
	}
	
	private double calculateTotalWeight() {
		return weights.values().stream().reduce(0D, Double::sum);
	}

	public Optional<T> getRandom(double chanceMultiplier) {
		double score = random.nextDouble();
		if(isWeighted)
			score *= calculateTotalWeight();
		if(chanceMultiplier > 0)
			score /= chanceMultiplier;
		List<Entry<T, Double>> entries = Collections.list(Collections.enumeration(weights.entrySet()));
		Collections.shuffle(entries);
		T key = null;
		for(Entry<T, Double> entry : entries) {
			score -= entry.getValue();
			final double goal = 0.0D;
			if(score <= goal) {
				key = entry.getKey();
				break;
			}
		}
		return Optional.ofNullable(key);
	}

	public Optional<T> getRandom() {
		return getRandom(1D);
	}
}
