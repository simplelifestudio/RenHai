package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * ImpressLabelCollection entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.ImpressLabelCollection
 * @author MyEclipse Persistence Tools
 */
public class ImpressLabelCollectionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(ImpressLabelCollectionDAO.class);
	// property constants
	public static final String IMPRESS_CARD_ID = "impressCardId";
	public static final String GLOBAL_IMPRESS_LABEL_ID = "globalImpressLabelId";
	public static final String COUNT = "count";
	public static final String UPDATE_TIME = "updateTime";
	public static final String ASSESS_COUNT = "assessCount";

	public void save(ImpressLabelCollection transientInstance) {
		log.debug("saving ImpressLabelCollection instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ImpressLabelCollection persistentInstance) {
		log.debug("deleting ImpressLabelCollection instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ImpressLabelCollection findById(java.lang.Integer id) {
		log.debug("getting ImpressLabelCollection instance with id: " + id);
		try {
			ImpressLabelCollection instance = (ImpressLabelCollection) getSession()
					.get("com.simplelife.renhai.server.db.ImpressLabelCollection",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ImpressLabelCollection> findByExample(
			ImpressLabelCollection instance) {
		log.debug("finding ImpressLabelCollection instance by example");
		try {
			List<ImpressLabelCollection> results = (List<ImpressLabelCollection>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.ImpressLabelCollection")
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
		log.debug("finding ImpressLabelCollection instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from ImpressLabelCollection as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<ImpressLabelCollection> findByImpressCardId(Object impressCardId) {
		return findByProperty(IMPRESS_CARD_ID, impressCardId);
	}

	public List<ImpressLabelCollection> findByGlobalImpressLabelId(
			Object globalImpressLabelId) {
		return findByProperty(GLOBAL_IMPRESS_LABEL_ID, globalImpressLabelId);
	}

	public List<ImpressLabelCollection> findByCount(Object count) {
		return findByProperty(COUNT, count);
	}

	public List<ImpressLabelCollection> findByUpdateTime(Object updateTime) {
		return findByProperty(UPDATE_TIME, updateTime);
	}

	public List<ImpressLabelCollection> findByAssessCount(Object assessCount) {
		return findByProperty(ASSESS_COUNT, assessCount);
	}

	public List findAll() {
		log.debug("finding all ImpressLabelCollection instances");
		try {
			String queryString = "from ImpressLabelCollection";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public ImpressLabelCollection merge(ImpressLabelCollection detachedInstance) {
		log.debug("merging ImpressLabelCollection instance");
		try {
			ImpressLabelCollection result = (ImpressLabelCollection) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ImpressLabelCollection instance) {
		log.debug("attaching dirty ImpressLabelCollection instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ImpressLabelCollection instance) {
		log.debug("attaching clean ImpressLabelCollection instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}