package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
public class TMcmPlatformInfo {

	private String orgId;
	private String orgName;
	private String orgShortName;
	private String filePushMode;
	private String sftpServer;
	private String sftpPort;
	private String sftpUser;
	private String sftpPwd;
	private String sftpPath;
	private String fileName;
	private String fileZipPwd;
	private String isTrade;
	private String state;
	private Date createTime;

	// Constructors

	/** default constructor */
	public TMcmPlatformInfo() {
	}

	/** minimal constructor */
	public TMcmPlatformInfo(String orgId) {
		this.orgId = orgId;
	}

	/** full constructor */
	public TMcmPlatformInfo(String orgId, String orgName,
			String orgShortName, String filePushMode, String sftpServer,
			String sftpPort, String sftpUser, String sftpPwd, String sftpPath,
			String fileName, String fileZipPwd, String isTrade, String state,Date createTime) {
		this.orgId = orgId;
		this.orgName = orgName;
		this.orgShortName = orgShortName;
		this.filePushMode = filePushMode;
		this.sftpServer = sftpServer;
		this.sftpPort = sftpPort;
		this.sftpUser = sftpUser;
		this.sftpPwd = sftpPwd;
		this.sftpPath = sftpPath;
		this.fileName = fileName;
		this.fileZipPwd = fileZipPwd;
		this.isTrade = isTrade;
		this.state = state;
		this.createTime = createTime;
	}


}