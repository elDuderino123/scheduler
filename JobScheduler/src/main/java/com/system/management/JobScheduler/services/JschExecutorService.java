package com.system.management.JobScheduler.services;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class JschExecutorService {

	private static Logger LOGGER = LoggerFactory.getLogger(JschExecutorService.class);
	private JSch jsch;
	

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		jsch = new JSch();
		try {
			if (!StringUtils.isBlank(env.getProperty("host.private.key"))) {
				jsch.addIdentity(env.getProperty("host.private.key"));
			}
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	public String sendCommand(String command, String host, int port, String user, String password) {
		Session session = null;
		Channel channel = null;
		StringBuilder output = new StringBuilder();
		try {
			session = jsch.getSession(user, host, port);
			if (password != null) {
				session.setPassword(password);
			}

			// Connect
			session.connect();

			// Open channel
			channel = session.openChannel("exec");

			((ChannelExec) channel).setCommand(command);

			// Open input stream to receive command output
			InputStream in = channel.getInputStream();

			// Connect the channel
			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					LOGGER.info("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
					LOGGER.error("Interrupted",ee);
					throw ee;
				}
			}
			

		} catch (Exception e) {
			LOGGER.error("Exception while executing the command",e);
			throw new RuntimeException("Exception while unning command "+ command, e);
			
		} finally{
			if(channel != null) {
				channel.disconnect();
			}
			if(session != null) {
				session.disconnect();
			}
		}
		
		return output.toString();
	}

}
