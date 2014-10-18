package model.serialization;

import model.statistics.StatisticsResult;

public interface StatisticsSerializer {

	public void serializeStatistics(StatisticsResult statistics);
}
