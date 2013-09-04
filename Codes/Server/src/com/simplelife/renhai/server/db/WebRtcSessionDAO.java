package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * WebRtcSession entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.WebRtcSession
 * @author MyEclipse Persistence Tools
 */
public class WebRtcSessionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(WebRtcSessionDAO.class);
	// property constants
	public static final String WEB_RTC_SESSION = "webRtcSession";
	public static final String REQUEST_DATE = "requestDate";
	public static final String TOKEN = "token";
	public static final String TOKEN_UPDATE_DATE = "tokenUpdateDate";
	public static final String EXPIRATION_DATE = "expirationDate";

	public void save(WebRtcSession transientInstance) {
		log.debug("saving WebRtcSession instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(WebRtcSession persistentInstance) {
		log.debug("deleting WebRtcSession instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public WebRtcSession findById(java.lang.Integer id) {
		log.debug("getting WebRtcSession instance with id: " + id);
		try {
			WebRtcSession instance = (WebRtcSession) getSession().get(
					"com.simplelife.renhai.server.db.WebRtcSession", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<WebRtcSession> findByExample(WebRtcSession instance) {
		log.debug("finding WebRtcSession instance by example");
		try {
			List<WebRtcSession> results = (List<WebRtcSession>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.WebRtcSession")
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
		log.debug("finding WebRtcSession instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from WebRtcSession as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<WebRtcSession> findByWebRtcSession(Object webRtcSession) {
		return findByProperty(WEB_RTC_SESSION, webRtcSession);
	}

	public List<WebRtcSession> findByRequestDate(Object requestDate) {
		return findByProperty(REQUEST_DATE, requestDate);
	}

	public List<WebRtcSession> findByToken(Object token) {
		return findByProperty(TOKEN, token);
	}

	public List<WebRtcSession> findByTokenUpdateDate(Object tokenUpdateDate) {
		return findByProperty(TOKEN_UPDATE_DATE, tokenUpdateDate);
	}

	public List<WebRtcSession> findByExpirationDate(Object expirationDate) {
		return findByProperty(EXPIRATION_DATE, expirationDate);
	}

	public List findAll() {
		log.debug("finding all WebRtcSession instances");
		try {
			String queryString = "from WebRtcSession";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public WebRtcSession merge(WebRtcSession detachedInstance) {
		log.debug("merging WebRtcSession instance");
		try {
			WebRtcSession result = (WebRtcSession) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(WebRtcSession instance) {
		log.debug("attaching dirty WebRtcSession instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(WebRtcSession instance) {
		log.debug("attaching clean WebRtcSession instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}