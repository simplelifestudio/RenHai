package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Statisticsitemdefinition entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.Statisticsitemdefinition
  * @author MyEclipse Persistence Tools 
 */
public class StatisticsitemdefinitionDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(StatisticsitemdefinitionDAO.class);
		//property constants
	public static final String STATISTICS_ITEM = "statisticsItem";
	public static final String DESCRIPTION = "description";



    
    public void save(Statisticsitemdefinition transientInstance) {
        log.debug("saving Statisticsitemdefinition instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Statisticsitemdefinition persistentInstance) {
        log.debug("deleting Statisticsitemdefinition instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Statisticsitemdefinition findById( java.lang.Integer id) {
        log.debug("getting Statisticsitemdefinition instance with id: " + id);
        try {
            Statisticsitemdefinition instance = (Statisticsitemdefinition) getSession()
                    .get("com.simplelife.renhai.server.db.Statisticsitemdefinition", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Statisticsitemdefinition> findByExample(Statisticsitemdefinition instance) {
        log.debug("finding Statisticsitemdefinition instance by example");
        try {
            List<Statisticsitemdefinition> results = (List<Statisticsitemdefinition>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Statisticsitemdefinition")
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
      log.debug("finding Statisticsitemdefinition instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Statisticsitemdefinition as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<Statisticsitemdefinition> findByStatisticsItem(Object statisticsItem
	) {
		return findByProperty(STATISTICS_ITEM, statisticsItem
		);
	}
	
	public List<Statisticsitemdefinition> findByDescription(Object description
	) {
		return findByProperty(DESCRIPTION, description
		);
	}
	

	public List findAll() {
		log.debug("finding all Statisticsitemdefinition instances");
		try {
			String queryString = "from Statisticsitemdefinition";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Statisticsitemdefinition merge(Statisticsitemdefinition detachedInstance) {
        log.debug("merging Statisticsitemdefinition instance");
        try {
            Statisticsitemdefinition result = (Statisticsitemdefinition) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Statisticsitemdefinition instance) {
        log.debug("attaching dirty Statisticsitemdefinition instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Statisticsitemdefinition instance) {
        log.debug("attaching clean Statisticsitemdefinition instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}