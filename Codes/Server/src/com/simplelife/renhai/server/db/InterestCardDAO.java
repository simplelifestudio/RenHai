package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * InterestCard entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.InterestCard
 * @author MyEclipse Persistence Tools
 */
public class InterestCardDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(InterestCardDAO.class);
	// property constants
	public static final String CREATE_TIME = "createTime";

	public void save(InterestCard transientInstance) {
		log.debug("saving InterestCard instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(InterestCard persistentInstance) {
		log.debug("deleting InterestCard instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public InterestCard findById(java.lang.Integer id) {
		log.debug("getting InterestCard instance with id: " + id);
		try {
			InterestCard instance = (InterestCard) getSession().get(
					"com.simplelife.renhai.server.db.InterestCard", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<InterestCard> findByExample(InterestCard instance) {
		log.debug("finding InterestCard instance by example");
		try {
			List<InterestCard> results = (List<InterestCard>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.InterestCard")
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
		log.debug("finding InterestCard instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from InterestCard as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<InterestCard> findByCreateTime(Object createTime) {
		return findByProperty(CREATE_TIME, createTime);
	}

	public List findAll() {
		log.debug("finding all InterestCard instances");
		try {
			String queryString = "from InterestCard";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public InterestCard merge(InterestCard detachedInstance) {
		log.debug("merging InterestCard instance");
		try {
			InterestCard result = (InterestCard) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(InterestCard instance) {
		log.debug("attaching dirty InterestCard instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(InterestCard instance) {
		log.debug("attaching clean InterestCard instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}