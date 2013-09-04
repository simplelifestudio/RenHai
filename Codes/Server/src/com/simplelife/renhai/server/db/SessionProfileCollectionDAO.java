package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * SessionProfileCollection entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.simplelife.renhai.server.db.SessionProfileCollection
 * @author MyEclipse Persistence Tools
 */
public class SessionProfileCollectionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SessionProfileCollectionDAO.class);
	// property constants
	public static final String SESSION_RECORD_ID = "sessionRecordId";
	public static final String PROFILE_ID = "profileId";
	public static final String COUNT = "count";

	public void save(SessionProfileCollection transientInstance) {
		log.debug("saving SessionProfileCollection instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(SessionProfileCollection persistentInstance) {
		log.debug("deleting SessionProfileCollection instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SessionProfileCollection findById(java.lang.Integer id) {
		log.debug("getting SessionProfileCollection instance with id: " + id);
		try {
			SessionProfileCollection instance = (SessionProfileCollection) getSession()
					.get("com.simplelife.renhai.server.db.SessionProfileCollection",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<SessionProfileCollection> findByExample(
			SessionProfileCollection instance) {
		log.debug("finding SessionProfileCollection instance by example");
		try {
			List<SessionProfileCollection> results = (List<SessionProfileCollection>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.SessionProfileCollection")
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
		log.debug("finding SessionProfileCollection instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from SessionProfileCollection as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<SessionProfileCollection> findBySessionRecordId(
			Object sessionRecordId) {
		return findByProperty(SESSION_RECORD_ID, sessionRecordId);
	}

	public List<SessionProfileCollection> findByProfileId(Object profileId) {
		return findByProperty(PROFILE_ID, profileId);
	}

	public List<SessionProfileCollection> findByCount(Object count) {
		return findByProperty(COUNT, count);
	}

	public List findAll() {
		log.debug("finding all SessionProfileCollection instances");
		try {
			String queryString = "from SessionProfileCollection";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public SessionProfileCollection merge(
			SessionProfileCollection detachedInstance) {
		log.debug("merging SessionProfileCollection instance");
		try {
			SessionProfileCollection result = (SessionProfileCollection) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(SessionProfileCollection instance) {
		log.debug("attaching dirty SessionProfileCollection instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(SessionProfileCollection instance) {
		log.debug("attaching clean SessionProfileCollection instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}