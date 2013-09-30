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
 * Impresslabelmap entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Impresslabelmap
 * @author MyEclipse Persistence Tools
 */
public class ImpresslabelmapDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(ImpresslabelmapDAO.class);
	// property constants
	public static final String ASSESSED_COUNT = "assessedCount";
	public static final String UPDATE_TIME = "updateTime";
	public static final String ASSESS_COUNT = "assessCount";

	public void save(Impresslabelmap transientInstance) {
		log.debug("saving Impresslabelmap instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Impresslabelmap persistentInstance) {
		log.debug("deleting Impresslabelmap instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Impresslabelmap findById(java.lang.Integer id) {
		log.debug("getting Impresslabelmap instance with id: " + id);
		try {
			Impresslabelmap instance = (Impresslabelmap) getSession().get(
					"com.simplelife.renhai.server.db.Impresslabelmap", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Impresslabelmap> findByExample(Impresslabelmap instance) {
		log.debug("finding Impresslabelmap instance by example");
		try {
			List<Impresslabelmap> results = (List<Impresslabelmap>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Impresslabelmap")
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
		log.debug("finding Impresslabelmap instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Impresslabelmap as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Impresslabelmap> findByAssessedCount(Object assessedCount) {
		return findByProperty(ASSESSED_COUNT, assessedCount);
	}

	public List<Impresslabelmap> findByUpdateTime(Object updateTime) {
		return findByProperty(UPDATE_TIME, updateTime);
	}

	public List<Impresslabelmap> findByAssessCount(Object assessCount) {
		return findByProperty(ASSESS_COUNT, assessCount);
	}

	public List findAll() {
		log.debug("finding all Impresslabelmap instances");
		try {
			String queryString = "from Impresslabelmap";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Impresslabelmap merge(Impresslabelmap detachedInstance) {
		log.debug("merging Impresslabelmap instance");
		try {
			Impresslabelmap result = (Impresslabelmap) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Impresslabelmap instance) {
		log.debug("attaching dirty Impresslabelmap instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Impresslabelmap instance) {
		log.debug("attaching clean Impresslabelmap instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}