package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Globalinterestlabel entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Globalinterestlabel
 * @author MyEclipse Persistence Tools
 */
public class GlobalinterestlabelDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(GlobalinterestlabelDAO.class);
	// property constants
	public static final String INTEREST_LABEL = "interestLabel";
	public static final String GLOBAL_MATCH_COUNT = "globalMatchCount";

	public void save(Globalinterestlabel transientInstance) {
		log.debug("saving Globalinterestlabel instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Globalinterestlabel persistentInstance) {
		log.debug("deleting Globalinterestlabel instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Globalinterestlabel findById(java.lang.Integer id) {
		log.debug("getting Globalinterestlabel instance with id: " + id);
		try {
			Globalinterestlabel instance = (Globalinterestlabel) getSession()
					.get("com.simplelife.renhai.server.db.Globalinterestlabel",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Globalinterestlabel> findByExample(Globalinterestlabel instance) {
		log.debug("finding Globalinterestlabel instance by example");
		try {
			List<Globalinterestlabel> results = (List<Globalinterestlabel>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Globalinterestlabel")
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
		log.debug("finding Globalinterestlabel instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Globalinterestlabel as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Globalinterestlabel> findByInterestLabel(Object interestLabel) {
		return findByProperty(INTEREST_LABEL, interestLabel);
	}

	public List<Globalinterestlabel> findByGlobalMatchCount(
			Object globalMatchCount) {
		return findByProperty(GLOBAL_MATCH_COUNT, globalMatchCount);
	}

	public List findAll() {
		log.debug("finding all Globalinterestlabel instances");
		try {
			String queryString = "from Globalinterestlabel";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Globalinterestlabel merge(Globalinterestlabel detachedInstance) {
		log.debug("merging Globalinterestlabel instance");
		try {
			Globalinterestlabel result = (Globalinterestlabel) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Globalinterestlabel instance) {
		log.debug("attaching dirty Globalinterestlabel instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Globalinterestlabel instance) {
		log.debug("attaching clean Globalinterestlabel instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}