package com.zcunsoft.tracking.report.entity.clickhouse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserReportBydateComId implements Serializable {
	private static final long serialVersionUID = 1L;

	private Timestamp stat_date;

	private  String lib;

	UserReportBydateComId() {
	}
	
	public UserReportBydateComId(Timestamp stat_date, String lib) {
		this.stat_date = stat_date;
		this.lib = lib;
	}

	public Timestamp getStat_date() {
		return stat_date;
	}

	public void setStat_date(Timestamp stat_date) {
		this.stat_date = stat_date;
	}

	public String getLib() {
		return lib;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.stat_date).append(this.lib).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserReportBydateComId == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		final UserReportBydateComId otherObject = (UserReportBydateComId) obj;

		return new EqualsBuilder().append(this.stat_date, otherObject.stat_date).append(this.lib, otherObject.lib)
				.isEquals();
	}
}