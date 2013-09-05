package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Impresslabelcollection entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.Impresslabelcollection
  * @author MyEclipse Persistence Tools 
 */
public class ImpresslabelcollectionDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(ImpresslabelcollectionDAO.class);
		//property constants
	public static final String COUNT = "count";
	public static final String UPDATE_TIME = "updateTime";
	public static final String ASSESS_COUNT = "assessCount";



    
    public void save(Impresslabelcollection transientInstance) {
        log.debug("saving Impresslabelcollection instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Impresslabelcollection persistentInstance) {
        log.debug("deleting Impresslabelcollection instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Impresslabelcollection findById( java.lang.Integer id) {
        log.debug("getting Impresslabelcollection instance with id: " + id);
        try {
            Impresslabelcollection instance = (Impresslabelcollection) getSession()
                    .get("com.simplelife.renhai.server.db.Impresslabelcollection", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Impresslabelcollection> findByExample(Impresslabelcollection instance) {
        log.debug("finding Impresslabelcollection instance by example");
        try {
            List<Impresslabelcollection> results = (List<Impresslabelcollection>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Impresslabelcollection")
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
      log.debug("finding Impresslabelcollection instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Impresslabelcollection as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<Impresslabelcollection> findByCount(Object count
	) {
		return findByProperty(COUNT, count
		);
	}
	
	public List<Impresslabelcollection> findByUpdateTime(Object updateTime
	) {
		return findByProperty(UPDATE_TIME, updateTime
		);
	}
	
	public List<Impresslabelcollection> findByAssessCount(Object assessCount
	) {
		return findByProperty(ASSESS_COUNT, assessCount
		);
	}
	

	public List findAll() {
		log.debug("finding all Impresslabelcollection instances");
		try {
			String queryString = "from Impresslabelcollection";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Impresslabelcollection merge(Impresslabelcollection detachedInstance) {
        log.debug("merging Impresslabelcollection instance");
        try {
            Impresslabelcollection result = (Impresslabelcollection) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Impresslabelcollection instance) {
        log.debug("attaching dirty Impresslabelcollection instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Impresslabelcollection instance) {
        log.debug("attaching clean Impresslabelcollection instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}