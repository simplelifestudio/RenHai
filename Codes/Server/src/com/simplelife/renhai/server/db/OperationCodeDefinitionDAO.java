package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * OperationCodeDefinition entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.OperationCodeDefinition
 * @author MyEclipse Persistence Tools
 */
public class OperationCodeDefinitionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(OperationCodeDefinitionDAO.class);
	// property constants
	public static final String OPERATION_CODE = "operationCode";
	public static final String OPERATION_TYPE = "operationType";
	public static final String DESCRIPTION = "description";

	public void save(OperationCodeDefinition transientInstance) {
		log.debug("saving OperationCodeDefinition instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(OperationCodeDefinition persistentInstance) {
		log.debug("deleting OperationCodeDefinition instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public OperationCodeDefinition findById(java.lang.Integer id) {
		log.debug("getting OperationCodeDefinition instance with id: " + id);
		try {
			OperationCodeDefinition instance = (OperationCodeDefinition) getSession()
					.get("com.simplelife.renhai.server.db.OperationCodeDefinition",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<OperationCodeDefinition> findByExample(
			OperationCodeDefinition instance) {
		log.debug("finding OperationCodeDefinition instance by example");
		try {
			List<OperationCodeDefinition> results = (List<OperationCodeDefinition>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.OperationCodeDefinition")
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
		log.debug("finding OperationCodeDefinition instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from OperationCodeDefinition as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<OperationCodeDefinition> findByOperationCode(
			Object operationCode) {
		return findByProperty(OPERATION_CODE, operationCode);
	}

	public List<OperationCodeDefinition> findByOperationType(
			Object operationType) {
		return findByProperty(OPERATION_TYPE, operationType);
	}

	public List<OperationCodeDefinition> findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findAll() {
		log.debug("finding all OperationCodeDefinition instances");
		try {
			String queryString = "from OperationCodeDefinition";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public OperationCodeDefinition merge(
			OperationCodeDefinition detachedInstance) {
		log.debug("merging OperationCodeDefinition instance");
		try {
			OperationCodeDefinition result = (OperationCodeDefinition) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(OperationCodeDefinition instance) {
		log.debug("attaching dirty OperationCodeDefinition instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(OperationCodeDefinition instance) {
		log.debug("attaching clean OperationCodeDefinition instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}