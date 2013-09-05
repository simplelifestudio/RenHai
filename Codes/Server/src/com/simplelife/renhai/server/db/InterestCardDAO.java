package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Interestcard entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.Interestcard
  * @author MyEclipse Persistence Tools 
 */
public class InterestcardDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(InterestcardDAO.class);
		//property constants
	public static final String CREATE_TIME = "createTime";



    
    public void save(Interestcard transientInstance) {
        log.debug("saving Interestcard instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Interestcard persistentInstance) {
        log.debug("deleting Interestcard instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Interestcard findById( java.lang.Integer id) {
        log.debug("getting Interestcard instance with id: " + id);
        try {
            Interestcard instance = (Interestcard) getSession()
                    .get("com.simplelife.renhai.server.db.Interestcard", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Interestcard> findByExample(Interestcard instance) {
        log.debug("finding Interestcard instance by example");
        try {
            List<Interestcard> results = (List<Interestcard>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Interestcard")
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
      log.debug("finding Interestcard instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Interestcard as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<Interestcard> findByCreateTime(Object createTime
	) {
		return findByProperty(CREATE_TIME, createTime
		);
	}
	

	public List findAll() {
		log.debug("finding all Interestcard instances");
		try {
			String queryString = "from Interestcard";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Interestcard merge(Interestcard detachedInstance) {
        log.debug("merging Interestcard instance");
        try {
            Interestcard result = (Interestcard) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Interestcard instance) {
        log.debug("attaching dirty Interestcard instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Interestcard instance) {
        log.debug("attaching clean Interestcard instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}