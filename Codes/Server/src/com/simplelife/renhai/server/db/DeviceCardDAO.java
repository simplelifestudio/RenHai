package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * DeviceCard entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.DeviceCard
 * @author MyEclipse Persistence Tools
 */
public class DeviceCardDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(DeviceCardDAO.class);
	// property constants
	public static final String DEVICE_SN = "deviceSn";
	public static final String REGISTER_TIME = "registerTime";
	public static final String SERVICE_STATUS = "serviceStatus";
	public static final String FORBIDDEN_EXPIRED_DATE = "forbiddenExpiredDate";
	public static final String PROFILE_ID = "profileId";
	public static final String DEVICE_MODEL = "deviceModel";
	public static final String OS_VERSION = "osVersion";
	public static final String APP_VERSION = "appVersion";
	public static final String LOCATION = "location";
	public static final String IS_JAILED = "isJailed";

	public void save(DeviceCard transientInstance) {
		log.debug("saving DeviceCard instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(DeviceCard persistentInstance) {
		log.debug("deleting DeviceCard instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public DeviceCard findById(java.lang.Integer id) {
		log.debug("getting DeviceCard instance with id: " + id);
		try {
			DeviceCard instance = (DeviceCard) getSession().get(
					"com.simplelife.renhai.server.db.DeviceCard", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<DeviceCard> findByExample(DeviceCard instance) {
		log.debug("finding DeviceCard instance by example");
		try {
			List<DeviceCard> results = (List<DeviceCard>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.DeviceCard")
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
		log.debug("finding DeviceCard instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from DeviceCard as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<DeviceCard> findByDeviceSn(Object deviceSn) {
		return findByProperty(DEVICE_SN, deviceSn);
	}

	public List<DeviceCard> findByRegisterTime(Object registerTime) {
		return findByProperty(REGISTER_TIME, registerTime);
	}

	public List<DeviceCard> findByServiceStatus(Object serviceStatus) {
		return findByProperty(SERVICE_STATUS, serviceStatus);
	}

	public List<DeviceCard> findByForbiddenExpiredDate(
			Object forbiddenExpiredDate) {
		return findByProperty(FORBIDDEN_EXPIRED_DATE, forbiddenExpiredDate);
	}

	public List<DeviceCard> findByProfileId(Object profileId) {
		return findByProperty(PROFILE_ID, profileId);
	}

	public List<DeviceCard> findByDeviceModel(Object deviceModel) {
		return findByProperty(DEVICE_MODEL, deviceModel);
	}

	public List<DeviceCard> findByOsVersion(Object osVersion) {
		return findByProperty(OS_VERSION, osVersion);
	}

	public List<DeviceCard> findByAppVersion(Object appVersion) {
		return findByProperty(APP_VERSION, appVersion);
	}

	public List<DeviceCard> findByLocation(Object location) {
		return findByProperty(LOCATION, location);
	}

	public List<DeviceCard> findByIsJailed(Object isJailed) {
		return findByProperty(IS_JAILED, isJailed);
	}

	public List findAll() {
		log.debug("finding all DeviceCard instances");
		try {
			String queryString = "from DeviceCard";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public DeviceCard merge(DeviceCard detachedInstance) {
		log.debug("merging DeviceCard instance");
		try {
			DeviceCard result = (DeviceCard) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(DeviceCard instance) {
		log.debug("attaching dirty DeviceCard instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(DeviceCard instance) {
		log.debug("attaching clean DeviceCard instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}