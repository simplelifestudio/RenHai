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
 * Sessionrecord entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Sessionrecord
 * @author MyEclipse Persistence Tools
 */
public class SessionrecordDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SessionrecordDAO.class);
	// property constants
	public static final String BUSINESS_TYPE = "businessType";
	public static final String SESSION_START_TIME = "sessionStartTime";
	public static final String SESSION_DURATION = "sessionDuration";
	public static final String CHAT_START_TIME = "chatStartTime";
	public static final String CHAT_DURATION = "chatDuration";
	public static final String END_STATUS = "endStatus";
	public static final String END_REASON = "endReason";

	public void save(Sessionrecord transientInstance) {
		log.debug("saving Sessionrecord instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Sessionrecord persistentInstance) {
		log.debug("deleting Sessionrecord instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Sessionrecord findById(java.lang.Integer id) {
		log.debug("getting Sessionrecord instance with id: " + id);
		try {
			Sessionrecord instance = (Sessionrecord) getSession().get(
					"com.simplelife.renhai.server.db.Sessionrecord", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Sessionrecord> findByExample(Sessionrecord instance) {
		log.debug("finding Sessionrecord instance by example");
		try {
			List<Sessionrecord> results = (List<Sessionrecord>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Sessionrecord")
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
		log.debug("finding Sessionrecord instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Sessionrecord as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Sessionrecord> findByBusinessType(Object businessType) {
		return findByProperty(BUSINESS_TYPE, businessType);
	}

	public List<Sessionrecord> findBySessionStartTime(Object sessionStartTime) {
		return findByProperty(SESSION_START_TIME, sessionStartTime);
	}

	public List<Sessionrecord> findBySessionDuration(Object sessionDuration) {
		return findByProperty(SESSION_DURATION, sessionDuration);
	}

	public List<Sessionrecord> findByChatStartTime(Object chatStartTime) {
		return findByProperty(CHAT_START_TIME, chatStartTime);
	}

	public List<Sessionrecord> findByChatDuration(Object chatDuration) {
		return findByProperty(CHAT_DURATION, chatDuration);
	}

	public List<Sessionrecord> findByEndStatus(Object endStatus) {
		return findByProperty(END_STATUS, endStatus);
	}

	public List<Sessionrecord> findByEndReason(Object endReason) {
		return findByProperty(END_REASON, endReason);
	}

	public List findAll() {
		log.debug("finding all Sessionrecord instances");
		try {
			String queryString = "from Sessionrecord";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Sessionrecord merge(Sessionrecord detachedInstance) {
		log.debug("merging Sessionrecord instance");
		try {
			Sessionrecord result = (Sessionrecord) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Sessionrecord instance) {
		log.debug("attaching dirty Sessionrecord instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Sessionrecord instance) {
		log.debug("attaching clean Sessionrecord instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}