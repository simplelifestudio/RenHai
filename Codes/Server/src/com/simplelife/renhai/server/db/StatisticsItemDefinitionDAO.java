package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * StatisticsItemDefinition entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.simplelife.renhai.server.db.StatisticsItemDefinition
 * @author MyEclipse Persistence Tools
 */
public class StatisticsItemDefinitionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(StatisticsItemDefinitionDAO.class);
	// property constants
	public static final String STATISTICS_ITEM = "statisticsItem";
	public static final String DESCRIPTION = "description";

	public void save(StatisticsItemDefinition transientInstance) {
		log.debug("saving StatisticsItemDefinition instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(StatisticsItemDefinition persistentInstance) {
		log.debug("deleting StatisticsItemDefinition instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public StatisticsItemDefinition findById(java.lang.Integer id) {
		log.debug("getting StatisticsItemDefinition instance with id: " + id);
		try {
			StatisticsItemDefinition instance = (StatisticsItemDefinition) getSession()
					.get("com.simplelife.renhai.server.db.StatisticsItemDefinition",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<StatisticsItemDefinition> findByExample(
			StatisticsItemDefinition instance) {
		log.debug("finding StatisticsItemDefinition instance by example");
		try {
			List<StatisticsItemDefinition> results = (List<StatisticsItemDefinition>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.StatisticsItemDefinition")
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
		log.debug("finding StatisticsItemDefinition instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from StatisticsItemDefinition as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<StatisticsItemDefinition> findByStatisticsItem(
			Object statisticsItem) {
		return findByProperty(STATISTICS_ITEM, statisticsItem);
	}

	public List<StatisticsItemDefinition> findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findAll() {
		log.debug("finding all StatisticsItemDefinition instances");
		try {
			String queryString = "from StatisticsItemDefinition";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public StatisticsItemDefinition merge(
			StatisticsItemDefinition detachedInstance) {
		log.debug("merging StatisticsItemDefinition instance");
		try {
			StatisticsItemDefinition result = (StatisticsItemDefinition) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(StatisticsItemDefinition instance) {
		log.debug("attaching dirty StatisticsItemDefinition instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(StatisticsItemDefinition instance) {
		log.debug("attaching clean StatisticsItemDefinition instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}