package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * SystemStatistics entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.SystemStatistics
 * @author MyEclipse Persistence Tools
 */
public class SystemStatisticsDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SystemStatisticsDAO.class);
	// property constants
	public static final String SAVE_TIME = "saveTime";
	public static final String STATISTICS_ITEM = "statisticsItem";
	public static final String COUNT = "count";

	public void save(SystemStatistics transientInstance) {
		log.debug("saving SystemStatistics instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(SystemStatistics persistentInstance) {
		log.debug("deleting SystemStatistics instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SystemStatistics findById(java.lang.Integer id) {
		log.debug("getting SystemStatistics instance with id: " + id);
		try {
			SystemStatistics instance = (SystemStatistics) getSession().get(
					"com.simplelife.renhai.server.db.SystemStatistics", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<SystemStatistics> findByExample(SystemStatistics instance) {
		log.debug("finding SystemStatistics instance by example");
		try {
			List<SystemStatistics> results = (List<SystemStatistics>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.SystemStatistics")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding SystemStatistics instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from SystemStatistics as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<SystemStatistics> findBySaveTime(Object saveTime) {
		return findByProperty(SAVE_TIME, saveTime);
	}

	public List<SystemStatistics> findByStatisticsItem(Object statisticsItem) {
		return findByProperty(STATISTICS_ITEM, statisticsItem);
	}

	public List<SystemStatistics> findByCount(Object count) {
		return findByProperty(COUNT, count);
	}

	public List findAll() {
		log.debug("finding all SystemStatistics instances");
		try {
			String queryString = "from SystemStatistics";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public SystemStatistics merge(SystemStatistics detachedInstance) {
		log.debug("merging SystemStatistics instance");
		try {
			SystemStatistics result = (SystemStatistics) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(SystemStatistics instance) {
		log.debug("attaching dirty SystemStatistics instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(SystemStatistics instance) {
		log.debug("attaching clean SystemStatistics instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}