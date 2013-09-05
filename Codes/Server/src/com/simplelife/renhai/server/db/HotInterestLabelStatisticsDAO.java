package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Hotinterestlabelstatistics entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.Hotinterestlabelstatistics
  * @author MyEclipse Persistence Tools 
 */
public class HotinterestlabelstatisticsDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(HotinterestlabelstatisticsDAO.class);
		//property constants
	public static final String SAVE_TIME = "saveTime";
	public static final String COUNT = "count";



    
    public void save(Hotinterestlabelstatistics transientInstance) {
        log.debug("saving Hotinterestlabelstatistics instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Hotinterestlabelstatistics persistentInstance) {
        log.debug("deleting Hotinterestlabelstatistics instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Hotinterestlabelstatistics findById( java.lang.Integer id) {
        log.debug("getting Hotinterestlabelstatistics instance with id: " + id);
        try {
            Hotinterestlabelstatistics instance = (Hotinterestlabelstatistics) getSession()
                    .get("com.simplelife.renhai.server.db.Hotinterestlabelstatistics", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Hotinterestlabelstatistics> findByExample(Hotinterestlabelstatistics instance) {
        log.debug("finding Hotinterestlabelstatistics instance by example");
        try {
            List<Hotinterestlabelstatistics> results = (List<Hotinterestlabelstatistics>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Hotinterestlabelstatistics")
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
      log.debug("finding Hotinterestlabelstatistics instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Hotinterestlabelstatistics as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<Hotinterestlabelstatistics> findBySaveTime(Object saveTime
	) {
		return findByProperty(SAVE_TIME, saveTime
		);
	}
	
	public List<Hotinterestlabelstatistics> findByCount(Object count
	) {
		return findByProperty(COUNT, count
		);
	}
	

	public List findAll() {
		log.debug("finding all Hotinterestlabelstatistics instances");
		try {
			String queryString = "from Hotinterestlabelstatistics";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Hotinterestlabelstatistics merge(Hotinterestlabelstatistics detachedInstance) {
        log.debug("merging Hotinterestlabelstatistics instance");
        try {
            Hotinterestlabelstatistics result = (Hotinterestlabelstatistics) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Hotinterestlabelstatistics instance) {
        log.debug("attaching dirty Hotinterestlabelstatistics instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Hotinterestlabelstatistics instance) {
        log.debug("attaching clean Hotinterestlabelstatistics instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}