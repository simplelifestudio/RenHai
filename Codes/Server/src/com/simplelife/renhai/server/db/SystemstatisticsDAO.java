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
 * Systemstatistics entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Systemstatistics
 * @author MyEclipse Persistence Tools
 */
public class SystemstatisticsDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SystemstatisticsDAO.class);
	// property constants
	public static final String SAVE_TIME = "saveTime";
	public static final String COUNT = "count";

	public void save(Systemstatistics transientInstance) {
		log.debug("saving Systemstatistics instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Systemstatistics persistentInstance) {
		log.debug("deleting Systemstatistics instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Systemstatistics findById(java.lang.Integer id) {
		log.debug("getting Systemstatistics instance with id: " + id);
		try {
			Systemstatistics instance = (Systemstatistics) getSession().get(
					"com.simplelife.renhai.server.db.Systemstatistics", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Systemstatistics> findByExample(Systemstatistics instance) {
		log.debug("finding Systemstatistics instance by example");
		try {
			List<Systemstatistics> results = (List<Systemstatistics>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Systemstatistics")
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
		log.debug("finding Systemstatistics instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Systemstatistics as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Systemstatistics> findBySaveTime(Object saveTime) {
		return findByProperty(SAVE_TIME, saveTime);
	}

	public List<Systemstatistics> findByCount(Object count) {
		return findByProperty(COUNT, count);
	}

	public List findAll() {
		log.debug("finding all Systemstatistics instances");
		try {
			String queryString = "from Systemstatistics";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Systemstatistics merge(Systemstatistics detachedInstance) {
		log.debug("merging Systemstatistics instance");
		try {
			Systemstatistics result = (Systemstatistics) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Systemstatistics instance) {
		log.debug("attaching dirty Systemstatistics instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Systemstatistics instance) {
		log.debug("attaching clean Systemstatistics instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}