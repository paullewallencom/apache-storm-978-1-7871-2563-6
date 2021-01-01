package com.stormadvance.stormredis;

import java.util.ArrayList;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class Topology {
	public static void main(String[] args) throws AlreadyAliveException,
			InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		
		List<String> zks = new ArrayList<String>();
		zks.add("192.168.41.122");
		
		List<String> cFs = new ArrayList<String>();
		cFs.add("personal");
		cFs.add("company");
		
		// set the spout class
		builder.setSpout("spout", new SampleSpout(), 2);
		// set the bolt class
		builder.setBolt("bolt", new StormRedisBolt("192.168.41.122",2181), 2).shuffleGrouping("spout");

		Config conf = new Config();
		conf.setDebug(true);
		// create an instance of LocalCluster class for
		// executing topology in local mode.
		LocalCluster cluster = new LocalCluster();

		// LearningStormTopolgy is the name of submitted topology.
		cluster.submitTopology("StormRedisTopology", conf,
				builder.createTopology());
		try {
			Thread.sleep(10000);
		} catch (Exception exception) {
			System.out.println("Thread interrupted exception : " + exception);
		}
		// kill the LearningStormTopology
		cluster.killTopology("StormRedisTopology");
		// shutdown the storm test cluster
		cluster.shutdown();
}
}