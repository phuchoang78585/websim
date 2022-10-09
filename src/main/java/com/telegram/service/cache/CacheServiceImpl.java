package com.telegram.service.cache;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.transcoders.SerializingTranscoder;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import net.spy.memcached.MemcachedClient;

@Service
public class CacheServiceImpl implements CacheService{

	private final MemcachedClient client;
	private final SerializingTranscoder tc;
	public CacheServiceImpl() {
		this.tc = new SerializingTranscoder() {

            @Override
            protected Object deserialize(byte[] data) {
                try {
                    return new ObjectInputStream(new ByteArrayInputStream(data)) {

                        @Override
                        protected Class<?> resolveClass(ObjectStreamClass desc)
                                throws IOException, ClassNotFoundException {
                            return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
                        }
                    }.readObject();
                } catch (Exception e) {
                    
                }
                return null;
            }

            @Override
            protected byte[] serialize(Object object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    new ObjectOutputStream(bos).writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    
                }
                return null;
            }
        };
	   MemcachedClient mc = null;
	   try {
		   AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },
			        new PlainCallbackHandler("memcached-memcachedcloud-acute-79920", "DXjXRwoJmQG8Q0zb3EtzMZvmYA9qlUIq"));
			
			   mc = new MemcachedClient(
			              new ConnectionFactoryBuilder()
			                  .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
			                  .setAuthDescriptor(ad).build(),
			          AddrUtil.getAddresses("memcached-12107.c270.us-east-1-3.ec2.cloud.redislabs.com:12107"));
	   } catch (Exception e) {
		// TODO: handle exception
	   }
	   this.client = mc;
		
	}

	 public void add(String key, Object value, int expiration) {
	        client.add(key, expiration, value, tc);
	    }

	    public Object get(String key) {
	        Future<Object> future = client.asyncGet(key, tc);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return null;
	    }

	    public void clear() {
	        client.flush();
	    }

	    public void delete(String key) {
	        client.delete(key);
	    }

	    public Map<String, Object> get(String[] keys) {
	        Future<Map<String, Object>> future = client.asyncGetBulk(tc, keys);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return Collections.<String, Object>emptyMap();
	    }

	    public long incr(String key, int by) {
	        return client.incr(key, by, 0);
	    }

	    public long decr(String key, int by) {
	        return client.decr(key, by, 0);
	    }

	    public void replace(String key, Object value, int expiration) {
	        client.replace(key, expiration, value, tc);
	    }

	    public boolean safeAdd(String key, Object value, int expiration) {
	        Future<Boolean> future = client.add(key, expiration, value, tc);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return false;
	    }

	    public boolean safeDelete(String key) {
	        Future<Boolean> future = client.delete(key);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return false;
	    }

	    public boolean safeReplace(String key, Object value, int expiration) {
	        Future<Boolean> future = client.replace(key, expiration, value, tc);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return false;
	    }

	    public boolean safeSet(String key, Object value, int expiration) {
	        Future<Boolean> future = client.set(key, expiration, value, tc);
	        try {
	            return future.get(1, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            future.cancel(false);
	        }
	        return false;
	    }

	    public void set(String key, Object value, int expiration) {
	        client.set(key, expiration, value, tc);
	    }

	  
	    public void stop() {
	        client.shutdown();
	    }

}
