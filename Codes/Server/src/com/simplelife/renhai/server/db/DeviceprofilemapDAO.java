package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Deviceprofilemap entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Deviceprofilemap
 * @author MyEclipse Persistence Tools
 */
public class DeviceprofilemapDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(DeviceprofilemapDAO.class);

	// property constants

	public void save(Deviceprofilemap transientInstance) {
		log.debug("saving Deviceprofilemap instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Deviceprofilemap persistentInstance) {
		log.debug("deleting Deviceprofilemap instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Deviceprofilemap findById(java.lang.Integer id) {
		log.debug("getting Deviceprofilemap instance with id: " + id);
		try {
			Deviceprofilemap instance = (Deviceprofilemap) getSession().get(
					"com.simplelife.renhai.server.db.Deviceprofilemap", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Deviceprofilemap> findByExample(Deviceprofilemap instance) {
		log.debug("finding Deviceprofilemap instance by example");
		try {
			List<Deviceprofilemap> results = (List<Deviceprofilemap>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Deviceprofilemap")
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
		log.debug("finding Deviceprofilemap instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Deviceprofilemap as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all Deviceprofilemap instances");
		try {
			String queryString = "from Deviceprofilemap";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Deviceprofilemap merge(Deviceprofilemap detachedInstance) {
		log.debug("merging Deviceprofilemap instance");
		try {
			Deviceprofilemap result = (Deviceprofilemap) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Deviceprofilemap instance) {
		log.debug("attaching dirty Deviceprofilemap instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Deviceprofilemap instance) {
		log.debug("attaching clean Deviceprofilemap instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}