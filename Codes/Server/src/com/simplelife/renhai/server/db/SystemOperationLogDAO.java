package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Systemoperationlog entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.SystemOperationLog
  * @author MyEclipse Persistence Tools 
 */
public class SystemOperationLogDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(SystemOperationLogDAO.class);
		//property constants
	public static final String LOG_TIME = "logTime";
	public static final String LOG_INFO = "logInfo";



    
    public void save(SystemOperationLog transientInstance) {
        log.debug("saving Systemoperationlog instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(SystemOperationLog persistentInstance) {
        log.debug("deleting Systemoperationlog instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public SystemOperationLog findById( java.lang.Integer id) {
        log.debug("getting Systemoperationlog instance with id: " + id);
        try {
            SystemOperationLog instance = (SystemOperationLog) getSession()
                    .get("com.simplelife.renhai.server.db.Systemoperationlog", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<SystemOperationLog> findByExample(SystemOperationLog instance) {
        log.debug("finding Systemoperationlog instance by example");
        try {
            List<SystemOperationLog> results = (List<SystemOperationLog>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Systemoperationlog")
                    .add( create(instance) )
            .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }    
    
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding Systemoperationlog instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Systemoperationlog as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<SystemOperationLog> findByLogTime(Object logTime
	) {
		return findByProperty(LOG_TIME, logTime
		);
	}
	
	public List<SystemOperationLog> findByLogInfo(Object logInfo
	) {
		return findByProperty(LOG_INFO, logInfo
		);
	}
	

	public List findAll() {
		log.debug("finding all Systemoperationlog instances");
		try {
			String queryString = "from Systemoperationlog";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public SystemOperationLog merge(SystemOperationLog detachedInstance) {
        log.debug("merging Systemoperationlog instance");
        try {
            SystemOperationLog result = (SystemOperationLog) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(SystemOperationLog instance) {
        log.debug("attaching dirty Systemoperationlog instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(SystemOperationLog instance) {
        log.debug("attaching clean Systemoperationlog instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}