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
 * Interestlabelmap entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Interestlabelmap
 * @author MyEclipse Persistence Tools
 */
public class InterestlabelmapDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(InterestlabelmapDAO.class);
	// property constants
	public static final String ORDER = "order";
	public static final String MATCH_COUNT = "matchCount";
	public static final String VALID_FLAG = "validFlag";

	public void save(Interestlabelmap transientInstance) {
		log.debug("saving Interestlabelmap instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Interestlabelmap persistentInstance) {
		log.debug("deleting Interestlabelmap instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Interestlabelmap findById(java.lang.Integer id) {
		log.debug("getting Interestlabelmap instance with id: " + id);
		try {
			Interestlabelmap instance = (Interestlabelmap) getSession().get(
					"com.simplelife.renhai.server.db.Interestlabelmap", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Interestlabelmap> findByExample(Interestlabelmap instance) {
		log.debug("finding Interestlabelmap instance by example");
		try {
			List<Interestlabelmap> results = (List<Interestlabelmap>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Interestlabelmap")
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
		log.debug("finding Interestlabelmap instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Interestlabelmap as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Interestlabelmap> findByOrder(Object order) {
		return findByProperty(ORDER, order);
	}

	public List<Interestlabelmap> findByMatchCount(Object matchCount) {
		return findByProperty(MATCH_COUNT, matchCount);
	}

	public List<Interestlabelmap> findByValidFlag(Object validFlag) {
		return findByProperty(VALID_FLAG, validFlag);
	}

	public List findAll() {
		log.debug("finding all Interestlabelmap instances");
		try {
			String queryString = "from Interestlabelmap";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Interestlabelmap merge(Interestlabelmap detachedInstance) {
		log.debug("merging Interestlabelmap instance");
		try {
			Interestlabelmap result = (Interestlabelmap) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Interestlabelmap instance) {
		log.debug("attaching dirty Interestlabelmap instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Interestlabelmap instance) {
		log.debug("attaching clean Interestlabelmap instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}