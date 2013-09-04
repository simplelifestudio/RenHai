package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * InterestLabelCollection entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.InterestLabelCollection
 * @author MyEclipse Persistence Tools
 */
public class InterestLabelCollectionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(InterestLabelCollectionDAO.class);
	// property constants
	public static final String INTEREST_CARD_ID = "interestCardId";
	public static final String GLOBAL_INTEREST_LABEL_ID = "globalInterestLabelId";
	public static final String ORDER = "order";
	public static final String MATCH_COUNT = "matchCount";
	public static final String VALID_FLAG = "validFlag";

	public void save(InterestLabelCollection transientInstance) {
		log.debug("saving InterestLabelCollection instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(InterestLabelCollection persistentInstance) {
		log.debug("deleting InterestLabelCollection instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public InterestLabelCollection findById(java.lang.Integer id) {
		log.debug("getting InterestLabelCollection instance with id: " + id);
		try {
			InterestLabelCollection instance = (InterestLabelCollection) getSession()
					.get("com.simplelife.renhai.server.db.InterestLabelCollection",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<InterestLabelCollection> findByExample(
			InterestLabelCollection instance) {
		log.debug("finding InterestLabelCollection instance by example");
		try {
			List<InterestLabelCollection> results = (List<InterestLabelCollection>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.InterestLabelCollection")
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
		log.debug("finding InterestLabelCollection instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from InterestLabelCollection as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<InterestLabelCollection> findByInterestCardId(
			Object interestCardId) {
		return findByProperty(INTEREST_CARD_ID, interestCardId);
	}

	public List<InterestLabelCollection> findByGlobalInterestLabelId(
			Object globalInterestLabelId) {
		return findByProperty(GLOBAL_INTEREST_LABEL_ID, globalInterestLabelId);
	}

	public List<InterestLabelCollection> findByOrder(Object order) {
		return findByProperty(ORDER, order);
	}

	public List<InterestLabelCollection> findByMatchCount(Object matchCount) {
		return findByProperty(MATCH_COUNT, matchCount);
	}

	public List<InterestLabelCollection> findByValidFlag(Object validFlag) {
		return findByProperty(VALID_FLAG, validFlag);
	}

	public List findAll() {
		log.debug("finding all InterestLabelCollection instances");
		try {
			String queryString = "from InterestLabelCollection";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public InterestLabelCollection merge(
			InterestLabelCollection detachedInstance) {
		log.debug("merging InterestLabelCollection instance");
		try {
			InterestLabelCollection result = (InterestLabelCollection) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(InterestLabelCollection instance) {
		log.debug("attaching dirty InterestLabelCollection instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(InterestLabelCollection instance) {
		log.debug("attaching clean InterestLabelCollection instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}