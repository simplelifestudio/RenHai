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
 * Operationcode entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.simplelife.renhai.server.db.Operationcode
 * @author MyEclipse Persistence Tools
 */
public class OperationcodeDAO extends AbstractHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(OperationcodeDAO.class);
	// property constants
	public static final String OPERATION_CODE = "operationCode";
	public static final String OPERATION_TYPE = "operationType";
	public static final String DESCRIPTION = "description";

	public void save(Operationcode transientInstance) {
		log.debug("saving Operationcode instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Operationcode persistentInstance) {
		log.debug("deleting Operationcode instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Operationcode findById(java.lang.Integer id) {
		log.debug("getting Operationcode instance with id: " + id);
		try {
			Operationcode instance = (Operationcode) getSession().get(
					"com.simplelife.renhai.server.db.Operationcode", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Operationcode> findByExample(Operationcode instance) {
		log.debug("finding Operationcode instance by example");
		try {
			List<Operationcode> results = (List<Operationcode>) getSession()
					.createCriteria(
							"com.simplelife.renhai.server.db.Operationcode")
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
		log.debug("finding Operationcode instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Operationcode as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Operationcode> findByOperationCode(Object operationCode) {
		return findByProperty(OPERATION_CODE, operationCode);
	}

	public List<Operationcode> findByOperationType(Object operationType) {
		return findByProperty(OPERATION_TYPE, operationType);
	}

	public List<Operationcode> findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findAll() {
		log.debug("finding all Operationcode instances");
		try {
			String queryString = "from Operationcode";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Operationcode merge(Operationcode detachedInstance) {
		log.debug("merging Operationcode instance");
		try {
			Operationcode result = (Operationcode) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Operationcode instance) {
		log.debug("attaching dirty Operationcode instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Operationcode instance) {
		log.debug("attaching clean Operationcode instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}