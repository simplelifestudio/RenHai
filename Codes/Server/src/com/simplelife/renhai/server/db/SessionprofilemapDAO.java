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
 * Sessionprofilemap entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Sessionprofilemap
 * @author MyEclipse Persistence Tools
 */
public class SessionprofilemapDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SessionprofilemapDAO.class);

	// property constants

	public void save(Sessionprofilemap transientInstance) {
		log.debug("saving Sessionprofilemap instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Sessionprofilemap persistentInstance) {
		log.debug("deleting Sessionprofilemap instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Sessionprofilemap findById(java.lang.Integer id) {
		log.debug("getting Sessionprofilemap instance with id: " + id);
		try {
			Sessionprofilemap instance = (Sessionprofilemap) getSession().get(
					"com.simplelife.renhai.server.db.Sessionprofilemap", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Sessionprofilemap> findByExample(Sessionprofilemap instance) {
		log.debug("finding Sessionprofilemap instance by example");
		try {
			List<Sessionprofilemap> results = (List<Sessionprofilemap>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Sessionprofilemap")
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
		log.debug("finding Sessionprofilemap instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Sessionprofilemap as model where model."
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
		log.debug("finding all Sessionprofilemap instances");
		try {
			String queryString = "from Sessionprofilemap";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Sessionprofilemap merge(Sessionprofilemap detachedInstance) {
		log.debug("merging Sessionprofilemap instance");
		try {
			Sessionprofilemap result = (Sessionprofilemap) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Sessionprofilemap instance) {
		log.debug("attaching dirty Sessionprofilemap instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Sessionprofilemap instance) {
		log.debug("attaching clean Sessionprofilemap instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}