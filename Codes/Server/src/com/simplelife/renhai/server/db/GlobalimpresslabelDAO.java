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
 * Globalimpresslabel entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Globalimpresslabel
 * @author MyEclipse Persistence Tools
 */
public class GlobalimpresslabelDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(GlobalimpresslabelDAO.class);
	// property constants
	public static final String IMPRESS_LABEL = "impressLabel";
	public static final String GLOBAL_ASSESS_COUNT = "globalAssessCount";

	public void save(Globalimpresslabel transientInstance) {
		log.debug("saving Globalimpresslabel instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Globalimpresslabel persistentInstance) {
		log.debug("deleting Globalimpresslabel instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Globalimpresslabel findById(java.lang.Integer id) {
		log.debug("getting Globalimpresslabel instance with id: " + id);
		try {
			Globalimpresslabel instance = (Globalimpresslabel) getSession()
					.get("com.simplelife.renhai.server.db.Globalimpresslabel",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Globalimpresslabel> findByExample(Globalimpresslabel instance) {
		log.debug("finding Globalimpresslabel instance by example");
		try {
			List<Globalimpresslabel> results = (List<Globalimpresslabel>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Globalimpresslabel")
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
		log.debug("finding Globalimpresslabel instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Globalimpresslabel as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Globalimpresslabel> findByImpressLabel(Object impressLabel) {
		return findByProperty(IMPRESS_LABEL, impressLabel);
	}

	public List<Globalimpresslabel> findByGlobalAssessCount(
			Object globalAssessCount) {
		return findByProperty(GLOBAL_ASSESS_COUNT, globalAssessCount);
	}

	public List findAll() {
		log.debug("finding all Globalimpresslabel instances");
		try {
			String queryString = "from Globalimpresslabel";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Globalimpresslabel merge(Globalimpresslabel detachedInstance) {
		log.debug("merging Globalimpresslabel instance");
		try {
			Globalimpresslabel result = (Globalimpresslabel) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Globalimpresslabel instance) {
		log.debug("attaching dirty Globalimpresslabel instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Globalimpresslabel instance) {
		log.debug("attaching clean Globalimpresslabel instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}