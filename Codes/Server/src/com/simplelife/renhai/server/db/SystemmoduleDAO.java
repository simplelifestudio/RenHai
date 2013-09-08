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
 * Systemmodule entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Systemmodule
 * @author MyEclipse Persistence Tools
 */
public class SystemmoduleDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(SystemmoduleDAO.class);
	// property constants
	public static final String MODULE_NO = "moduleNo";
	public static final String DESCRIPTION = "description";

	public void save(Systemmodule transientInstance) {
		log.debug("saving Systemmodule instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Systemmodule persistentInstance) {
		log.debug("deleting Systemmodule instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Systemmodule findById(java.lang.Integer id) {
		log.debug("getting Systemmodule instance with id: " + id);
		try {
			Systemmodule instance = (Systemmodule) getSession().get(
					"com.simplelife.renhai.server.db.Systemmodule", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Systemmodule> findByExample(Systemmodule instance) {
		log.debug("finding Systemmodule instance by example");
		try {
			List<Systemmodule> results = (List<Systemmodule>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Systemmodule")
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
		log.debug("finding Systemmodule instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Systemmodule as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Systemmodule> findByModuleNo(Object moduleNo) {
		return findByProperty(MODULE_NO, moduleNo);
	}

	public List<Systemmodule> findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findAll() {
		log.debug("finding all Systemmodule instances");
		try {
			String queryString = "from Systemmodule";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Systemmodule merge(Systemmodule detachedInstance) {
		log.debug("merging Systemmodule instance");
		try {
			Systemmodule result = (Systemmodule) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Systemmodule instance) {
		log.debug("attaching dirty Systemmodule instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Systemmodule instance) {
		log.debug("attaching clean Systemmodule instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}