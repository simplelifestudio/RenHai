package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.AbstractHibernateDAO;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Statisticsitem entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Statisticsitem
 * @author MyEclipse Persistence Tools
 */
public class StatisticsitemDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(StatisticsitemDAO.class);
	// property constants
	public static final String STATISTICS_ITEM = "statisticsItem";
	public static final String DESCRIPTION = "description";

	public void save(Statisticsitem transientInstance) {
		log.debug("saving Statisticsitem instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Statisticsitem persistentInstance) {
		log.debug("deleting Statisticsitem instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Statisticsitem findById(java.lang.Integer id) {
		log.debug("getting Statisticsitem instance with id: " + id);
		try {
			Statisticsitem instance = (Statisticsitem) getSession().get(
					"com.simplelife.renhai.server.db.Statisticsitem", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Statisticsitem> findByExample(Statisticsitem instance) {
		log.debug("finding Statisticsitem instance by example");
		try {
			List<Statisticsitem> results = (List<Statisticsitem>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Statisticsitem")
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
		log.debug("finding Statisticsitem instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Statisticsitem as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Statisticsitem> findByStatisticsItem(Object statisticsItem) {
		return findByProperty(STATISTICS_ITEM, statisticsItem);
	}

	public List<Statisticsitem> findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findAll() {
		log.debug("finding all Statisticsitem instances");
		try {
			String queryString = "from Statisticsitem";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Statisticsitem merge(Statisticsitem detachedInstance) {
		log.debug("merging Statisticsitem instance");
		try {
			Statisticsitem result = (Statisticsitem) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Statisticsitem instance) {
		log.debug("attaching dirty Statisticsitem instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Statisticsitem instance) {
		log.debug("attaching clean Statisticsitem instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}