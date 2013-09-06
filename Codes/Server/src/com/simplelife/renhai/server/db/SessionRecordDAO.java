package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Sessionrecord entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.SessionRecord
  * @author MyEclipse Persistence Tools 
 */
public class SessionRecordDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(SessionRecordDAO.class);
		//property constants
	public static final String BUSINESS_TYPE = "businessType";
	public static final String START_TIME = "startTime";
	public static final String DURATION = "duration";
	public static final String END_STATUS = "endStatus";
	public static final String END_REASON = "endReason";



    
    public void save(SessionRecord transientInstance) {
        log.debug("saving Sessionrecord instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(SessionRecord persistentInstance) {
        log.debug("deleting Sessionrecord instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public SessionRecord findById( java.lang.Integer id) {
        log.debug("getting Sessionrecord instance with id: " + id);
        try {
            SessionRecord instance = (SessionRecord) getSession()
                    .get("com.simplelife.renhai.server.db.Sessionrecord", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<SessionRecord> findByExample(SessionRecord instance) {
        log.debug("finding Sessionrecord instance by example");
        try {
            List<SessionRecord> results = (List<SessionRecord>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Sessionrecord")
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
      log.debug("finding Sessionrecord instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Sessionrecord as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<SessionRecord> findByBusinessType(Object businessType
	) {
		return findByProperty(BUSINESS_TYPE, businessType
		);
	}
	
	public List<SessionRecord> findByStartTime(Object startTime
	) {
		return findByProperty(START_TIME, startTime
		);
	}
	
	public List<SessionRecord> findByDuration(Object duration
	) {
		return findByProperty(DURATION, duration
		);
	}
	
	public List<SessionRecord> findByEndStatus(Object endStatus
	) {
		return findByProperty(END_STATUS, endStatus
		);
	}
	
	public List<SessionRecord> findByEndReason(Object endReason
	) {
		return findByProperty(END_REASON, endReason
		);
	}
	

	public List findAll() {
		log.debug("finding all Sessionrecord instances");
		try {
			String queryString = "from Sessionrecord";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public SessionRecord merge(SessionRecord detachedInstance) {
        log.debug("merging Sessionrecord instance");
        try {
            SessionRecord result = (SessionRecord) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(SessionRecord instance) {
        log.debug("attaching dirty Sessionrecord instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(SessionRecord instance) {
        log.debug("attaching clean Sessionrecord instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}