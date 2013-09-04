package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * ProfileOperationLog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.ProfileOperationLog
 * @author MyEclipse Persistence Tools
 */
public class ProfileOperationLogDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(ProfileOperationLogDAO.class);
	// property constants
	public static final String DEVICE_ID = "deviceId";
	public static final String PROFILE_ID = "profileId";
	public static final String LOG_TIME = "logTime";
	public static final String OPERATION_CODE = "operationCode";
	public static final String LOG_INFO = "logInfo";

	public void save(ProfileOperationLog transientInstance) {
		log.debug("saving ProfileOperationLog instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ProfileOperationLog persistentInstance) {
		log.debug("deleting ProfileOperationLog instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ProfileOperationLog findById(java.lang.Integer id) {
		log.debug("getting ProfileOperationLog instance with id: " + id);
		try {
			ProfileOperationLog instance = (ProfileOperationLog) getSession()
					.get("com.simplelife.renhai.server.db.ProfileOperationLog",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ProfileOperationLog> findByExample(ProfileOperationLog instance) {
		log.debug("finding ProfileOperationLog instance by example");
		try {
			List<ProfileOperationLog> results = (List<ProfileOperationLog>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.ProfileOperationLog")
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
		log.debug("finding ProfileOperationLog instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from ProfileOperationLog as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<ProfileOperationLog> findByDeviceId(Object deviceId) {
		return findByProperty(DEVICE_ID, deviceId);
	}

	public List<ProfileOperationLog> findByProfileId(Object profileId) {
		return findByProperty(PROFILE_ID, profileId);
	}

	public List<ProfileOperationLog> findByLogTime(Object logTime) {
		return findByProperty(LOG_TIME, logTime);
	}

	public List<ProfileOperationLog> findByOperationCode(Object operationCode) {
		return findByProperty(OPERATION_CODE, operationCode);
	}

	public List<ProfileOperationLog> findByLogInfo(Object logInfo) {
		return findByProperty(LOG_INFO, logInfo);
	}

	public List findAll() {
		log.debug("finding all ProfileOperationLog instances");
		try {
			String queryString = "from ProfileOperationLog";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public ProfileOperationLog merge(ProfileOperationLog detachedInstance) {
		log.debug("merging ProfileOperationLog instance");
		try {
			ProfileOperationLog result = (ProfileOperationLog) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ProfileOperationLog instance) {
		log.debug("attaching dirty ProfileOperationLog instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ProfileOperationLog instance) {
		log.debug("attaching clean ProfileOperationLog instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}