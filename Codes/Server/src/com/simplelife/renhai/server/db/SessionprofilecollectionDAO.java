package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Sessionprofilecollection entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.simplelife.renhai.server.db.Sessionprofilecollection
 * @author MyEclipse Persistence Tools
 */
public class SessionprofilecollectionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SessionprofilecollectionDAO.class);
	// property constants
	public static final String COUNT = "count";

	public void save(Sessionprofilecollection transientInstance) {
		log.debug("saving Sessionprofilecollection instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Sessionprofilecollection persistentInstance) {
		log.debug("deleting Sessionprofilecollection instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Sessionprofilecollection findById(java.lang.Integer id) {
		log.debug("getting Sessionprofilecollection instance with id: " + id);
		try {
			Sessionprofilecollection instance = (Sessionprofilecollection) getSession()
					.get("com.simplelife.renhai.server.db.Sessionprofilecollection",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Sessionprofilecollection> findByExample(
			Sessionprofilecollection instance) {
		log.debug("finding Sessionprofilecollection instance by example");
		try {
			List<Sessionprofilecollection> results = (List<Sessionprofilecollection>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Sessionprofilecollection")
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
		log.debug("finding Sessionprofilecollection instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Sessionprofilecollection as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Sessionprofilecollection> findByCount(Object count) {
		return findByProperty(COUNT, count);
	}

	public List findAll() {
		log.debug("finding all Sessionprofilecollection instances");
		try {
			String queryString = "from Sessionprofilecollection";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Sessionprofilecollection merge(
			Sessionprofilecollection detachedInstance) {
		log.debug("merging Sessionprofilecollection instance");
		try {
			Sessionprofilecollection result = (Sessionprofilecollection) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Sessionprofilecollection instance) {
		log.debug("attaching dirty Sessionprofilecollection instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Sessionprofilecollection instance) {
		log.debug("attaching clean Sessionprofilecollection instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}