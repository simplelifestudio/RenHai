package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * DeviceProfileMap entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.DeviceProfileMap
 * @author MyEclipse Persistence Tools
 */
public class DeviceProfileMapDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(DeviceProfileMapDAO.class);
	// property constants
	public static final String DEVICE_ID = "deviceId";
	public static final String PROFILE_ID = "profileId";

	public void save(DeviceProfileMap transientInstance) {
		log.debug("saving DeviceProfileMap instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(DeviceProfileMap persistentInstance) {
		log.debug("deleting DeviceProfileMap instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public DeviceProfileMap findById(java.lang.Integer id) {
		log.debug("getting DeviceProfileMap instance with id: " + id);
		try {
			DeviceProfileMap instance = (DeviceProfileMap) getSession().get(
					"com.simplelife.renhai.server.db.DeviceProfileMap", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<DeviceProfileMap> findByExample(DeviceProfileMap instance) {
		log.debug("finding DeviceProfileMap instance by example");
		try {
			List<DeviceProfileMap> results = (List<DeviceProfileMap>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.DeviceProfileMap")
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
		log.debug("finding DeviceProfileMap instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from DeviceProfileMap as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<DeviceProfileMap> findByDeviceId(Object deviceId) {
		return findByProperty(DEVICE_ID, deviceId);
	}

	public List<DeviceProfileMap> findByProfileId(Object profileId) {
		return findByProperty(PROFILE_ID, profileId);
	}

	public List findAll() {
		log.debug("finding all DeviceProfileMap instances");
		try {
			String queryString = "from DeviceProfileMap";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public DeviceProfileMap merge(DeviceProfileMap detachedInstance) {
		log.debug("merging DeviceProfileMap instance");
		try {
			DeviceProfileMap result = (DeviceProfileMap) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(DeviceProfileMap instance) {
		log.debug("attaching dirty DeviceProfileMap instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(DeviceProfileMap instance) {
		log.debug("attaching clean DeviceProfileMap instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}