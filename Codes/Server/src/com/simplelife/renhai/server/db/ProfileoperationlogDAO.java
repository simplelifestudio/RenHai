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
 * Profileoperationlog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Profileoperationlog
 * @author MyEclipse Persistence Tools
 */
public class ProfileoperationlogDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(ProfileoperationlogDAO.class);
	// property constants
	public static final String LOG_TIME = "logTime";
	public static final String LOG_INFO = "logInfo";

	public void save(Profileoperationlog transientInstance) {
		log.debug("saving Profileoperationlog instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Profileoperationlog persistentInstance) {
		log.debug("deleting Profileoperationlog instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Profileoperationlog findById(java.lang.Integer id) {
		log.debug("getting Profileoperationlog instance with id: " + id);
		try {
			Profileoperationlog instance = (Profileoperationlog) getSession()
					.get("com.simplelife.renhai.server.db.Profileoperationlog",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Profileoperationlog> findByExample(Profileoperationlog instance) {
		log.debug("finding Profileoperationlog instance by example");
		try {
			List<Profileoperationlog> results = (List<Profileoperationlog>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Profileoperationlog")
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
		log.debug("finding Profileoperationlog instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Profileoperationlog as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Profileoperationlog> findByLogTime(Object logTime) {
		return findByProperty(LOG_TIME, logTime);
	}

	public List<Profileoperationlog> findByLogInfo(Object logInfo) {
		return findByProperty(LOG_INFO, logInfo);
	}

	public List findAll() {
		log.debug("finding all Profileoperationlog instances");
		try {
			String queryString = "from Profileoperationlog";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Profileoperationlog merge(Profileoperationlog detachedInstance) {
		log.debug("merging Profileoperationlog instance");
		try {
			Profileoperationlog result = (Profileoperationlog) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Profileoperationlog instance) {
		log.debug("attaching dirty Profileoperationlog instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Profileoperationlog instance) {
		log.debug("attaching clean Profileoperationlog instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}