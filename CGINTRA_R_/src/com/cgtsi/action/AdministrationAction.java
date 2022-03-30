/*************************************************************
 *
 * Name of the class: AdministrationAction.java
 * This class intercepts all the requests passed on to Administration, Registration
 * and Common Module's business layers. This class converts the jsp page's values into
 * Value Objects and does the neccessary business operations. Also, it converts the
 * Value Object values into action form values.
 *
 *
 * @author : Veldurai T
 * @version:
 * @since: Oct 8, 2003
 **************************************************************/

package com.cgtsi.action;

import com.cgtsi.common.CommonDAO;
import java.io.*;
// import java.io.BufferedReader;
import com.cgtsi.common.InvalidFileFormatException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import com.cgtsi.application.Application;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.StringTokenizer;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import com.cgtsi.util.CustomisedDate;
import com.cgtsi.util.DBConnection;
import com.cgtsi.admin.InvalidDataException;
import com.cgtsi.actionform.AdministrationActionForm;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.Message;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.PasswordManager;
import com.cgtsi.admin.Privileges;
import com.cgtsi.admin.Role;
import com.cgtsi.admin.User;
import com.cgtsi.common.AuditDetails;
import com.cgtsi.common.CommonUtility;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.FileUploader;
import com.cgtsi.common.InactiveUserException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.common.NoUserFoundException;
import com.cgtsi.common.UploadFailedException;
import com.cgtsi.registration.CollectingBank;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.Registration;
import com.cgtsi.util.SessionConstants;
import com.cgtsi.admin.AdminConstants;
import com.cgtsi.admin.AdminDAO;
import com.cgtsi.admin.MenuOptions_back;
import com.cgtsi.admin.ScheduledProcessManager;
import com.cgtsi.admin.ZoneMaster;
import com.cgtsi.admin.RegionMaster;
import com.cgtsi.admin.StateMaster;
import com.cgtsi.admin.DistrictMaster;
import com.cgtsi.admin.DesignationMaster;
import com.cgtsi.admin.Hint;
import com.cgtsi.admin.PLRMaster;
import com.cgtsi.admin.AlertMaster;
import com.cgtsi.admin.ExceptionMaster;
import com.cgtsi.admin.BroadCastMessage;
import com.cgtsi.receiptspayments.RpProcessor;

import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.registration.NoMemberFoundException;
import com.cgtsi.common.MailerException;

/**
 * @author Veldurai T
 * 
 *         This class intercepts all the requests passed on to Administration,
 *         Registration and Common Module's business layers. This class converts
 *         the jsp page's values into Value Objects and does the neccessary
 *         business operations. Also, it converts the Value Object values into
 *         action form values.
 */
public class AdministrationAction extends BaseAction {

	// This method is for uploading a file.
	public ActionForward upload(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession(false);

		FileUploading fileUpload = (FileUploading) session
				.getAttribute("fileUploading");
		fileUpload.setRequest(request);

		Integer uploadStatus = (Integer) session
				.getAttribute(SessionConstants.FILE_UPLOAD_STATUS);

		Log.log(Log.INFO, "AdministrationAction", "upload", "Entered"
				+ SessionConstants.FILE_UPLOAD_STATUS);

		Log.log(Log.DEBUG, "AdministrationAction", "upload", "uploadStatus "
				+ uploadStatus);

		if (uploadStatus != null) {
			if (uploadStatus.intValue() == Constants.FILE_UPLOAD_SUCCESS) {
				Log.log(Log.INFO, "AdministrationAction", "upload",
						"Exited..success");
				return mapping.findForward("success");
			} else if (uploadStatus.intValue() == Constants.FILE_UPLOAD_FAILED) {
				Log.log(Log.INFO, "AdministrationAction", "upload",
						"Exited..upload error");

				throw new UploadFailedException("Upload Failed....");
			} else {
				Log.log(Log.INFO, "AdministrationAction", "upload",
						"Exited..failed");

				return mapping.findForward("failed");
			}
		} else {
			Log.log(Log.INFO, "AdministrationAction", "upload",
					"Exited..failed");

			return mapping.findForward("failed");
		}
	}

	/**
	 * This method is for adding a role.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	
	public ActionForward showAddRole(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showAddRole", "Entered");

		Administrator admin = new Administrator();

		ArrayList privileges = admin.getAllPrivileges();

		if (privileges != null) {
			Log.log(Log.DEBUG, "AdministrationAction", "showAddRole",
					"privileges size is: " + privileges.size());
		}

		DynaActionForm dynaForm = (DynaActionForm) form;

		dynaForm.initialize(mapping);

		// dynaForm.set("privileges",privileges);

		Log.log(Log.INFO, "AdministrationAction", "showAddRole", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for modifying a Role.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showModifyRole(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyRole", "Entered");
		Administrator admin = new Administrator();
		ArrayList roles = admin.getAllRoles();
		ArrayList roleNames = new ArrayList();
		if (roles != null) {

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyRole",
					"roles size is: " + roles.size());

			for (int i = 0; i < roles.size(); i++) {
				Role role = (Role) roles.get(i);

				if (role != null) {
					String roleName = role.getRoleName();
					if ((!roleName.equalsIgnoreCase("DEMOUSER"))
							&& (!roleName.equalsIgnoreCase("AUDITOR"))) {
						// Log.log(Log.INFO,"AdministrationAction","showModifyRole","role.getRoleName() :"
						// + role.getRoleName());
						roleNames.add(roleName);
					}
				}
			}
		}

		DynaActionForm dynaForm = (DynaActionForm) form;
		// String roleName=(String)dynaForm.get("roleName");

		dynaForm.initialize(mapping);

		// dynaForm.set("roles",roleNames);
		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.ROLE_NAMES, roleNames);
		// dynaForm.set("roleName",roleName);

		roles = null;
		roleNames = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyRole", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for fetching the privileges for a particular Role.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward getPrivilegesForRole(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getPrivilegesForRole",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator admin = new Administrator();

		ArrayList roles = admin.getAllRoles();
		ArrayList roleNames = new ArrayList();

		String roleDescription = "";
		String roleName = (String) dynaForm.get("roleName");

		Log.log(Log.DEBUG, "AdministrationAction", "getPrivilegesForRole",
				" role name is  " + roleName);

		if (roles != null) {

			Log.log(Log.DEBUG, "AdministrationAction", "getPrivilegesForRole",
					"roles size is: " + roles.size());

			for (int i = 0; i < roles.size(); i++) {
				Role role = (Role) roles.get(i);

				if (role != null) {
					roleNames.add(role.getRoleName());

					Log.log(Log.DEBUG, "AdministrationAction",
							"getPrivilegesForRole",
							" role name...  " + role.getRoleName());

					if (roleName.equals(role.getRoleName())) {
						roleDescription = role.getRoleDescription();

						Log.log(Log.DEBUG, "AdministrationAction",
								"getPrivilegesForRole", " role desc is  "
										+ roleDescription);
					}
				}
			}
		}

		ArrayList privileges = admin.getPrivilegesForRole(roleName);

		dynaForm.initialize(mapping);

		dynaForm.set("roleNames", roleNames);
		dynaForm.set("roleName", roleName);
		dynaForm.set("roleDescription", roleDescription);

		if (privileges != null) {
			for (int i = 0; i < privileges.size(); i++) {
				String privilege = (String) privileges.get(i);

				Log.log(Log.DEBUG, "AdministrationAction",
						"getPrivilegesForRole", " privilege is  " + privilege);

				dynaForm.set(privilege, privilege);
			}
		}

		roles = null;
		roleNames = null;
		privileges = null;

		Log.log(Log.INFO, "AdministrationAction", "getPrivilegesForRole",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for displaying the Password on the screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward displayPassword(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "displayPassword", "Entered");
		String password = "Password Will be displayed here.";

		request.setAttribute("displayPassword", password);
		Log.log(Log.INFO, "AdministrationAction", "displayPassword", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the screen for entering CGTSI user details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showCGTSIUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showCGTSIUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator admin = new Administrator();

		// Populate the designations combo box.
		ArrayList designation = admin.getAllDesignations();
		dynaForm.set("designations", designation);

		designation = null;

		Log.log(Log.INFO, "AdministrationAction", "showCGTSIUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is for adding the entered CGTSI user details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward addCGTSIUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addCGTSIUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		User user = new User();

		BeanUtils.populate(user, dynaForm.getMap());

		// Set the Bank,zone and branch Ids to '0000' for CGTSI user.
		user.setBankId(Constants.CGTSI_USER_BANK_ID);
		user.setZoneId(Constants.CGTSI_USER_ZONE_ID);
		user.setBranchId(Constants.CGTSI_USER_BRANCH_ID);

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		String memberId = Constants.CGTSI_MEMBERID;
		String userId = null;

		try {
			userId = admin.createUser(createdBy, user, true);
			request.setAttribute("message", "MemberId is " + memberId
					+ "  UserId is " + userId);
		}

		catch (MailerException mailerException) {

			String errorMessage = mailerException.getMessage();
			request.setAttribute("message", errorMessage + "  MemberId is  "
					+ memberId);
		}

		user = null;
		creatingUser = null;

		Log.log(Log.INFO, "AdministrationAction", "addCGTSIUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is for displaying screen for entering new MLI user details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showMLIUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showMLIUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator admin = new Administrator();

		ArrayList designation = admin.getAllDesignations();
		dynaForm.set("designations", designation);

		designation = null;

		Log.log(Log.INFO, "AdministrationAction", "showMLIUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method adds the entered MLI user details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward addMLIUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addMLIUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		User user = new User();

		BeanUtils.populate(user, dynaForm.getMap());

		// Get the bank id,zone id and branch id entered in the screen.
		String bankId = (String) dynaForm.get("bankId");
		String zoneId = (String) dynaForm.get("zoneId");
		String branchId = (String) dynaForm.get("branchId");
		String memberId = bankId + zoneId + branchId;

		// Validate if the entered bank id exists by calling this method.
		Registration registration = new Registration();
		registration.getMemberDetails(bankId, zoneId, branchId);

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		// Validate that the bank id of the logged in user is the same as the
		// one entered.

		String loggedInUserBankId = creatingUser.getBankId();

		if (loggedInUserBankId.equals(Constants.CGTSI_USER_BANK_ID)
				|| loggedInUserBankId.equals(bankId)) {

			String userId = null;
			try {
				userId = admin.createUser(createdBy, user, true);
				request.setAttribute("message", "MemberId is " + memberId
						+ "  UserId is " + userId);
			}

			catch (MailerException mailerException) {
				String errorMessage = mailerException.getMessage();
				request.setAttribute("message", errorMessage
						+ "  MemberId is  " + memberId);
			}
		}

		else {

			request.setAttribute("message", "Cannot create users for other MLI");

		}

		user = null;
		creatingUser = null;

		Log.log(Log.INFO, "AdministrationAction", "addMLIUser", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method displays the screen where the user whose details are to be
	 * modified is chosen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showModifyUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showmodifyUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		// Get the logged in user's memberId.
		User loggedUser = getUserInformation(request);
		String userBankId = loggedUser.getBankId();
		String userZoneId = loggedUser.getZoneId();
		String userBranchId = loggedUser.getBranchId();
		String userMemberId = userBankId + userZoneId + userBranchId;

		Registration registration = new Registration();
		ArrayList memberIds = new ArrayList();
		ArrayList zones = null;
		ArrayList branches = null;
		ArrayList allMembers = null;
		MLIInfo mliInfo = null;

		// Add the logged in users member id to the combo box.
		memberIds.add(userMemberId);

		if (userBankId.equals("0000") && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			allMembers = registration.getAllMembers();
			int memberSize = allMembers.size();
			for (int i = 0; i < memberSize; i++) {
				mliInfo = (MLIInfo) allMembers.get(i);
				String bankId = mliInfo.getBankId();
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = bankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		}

		else if (!(userBankId.equals("0000")) && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			// Get the zones for the MLI to which the logged in user belongs.
			zones = registration.getZones(userBankId);
			int zoneSize = zones.size();
			for (int i = 0; i < zoneSize; i++) {
				mliInfo = (MLIInfo) zones.get(i);
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		} else if (!(userBankId.equals("0000")) && !(userZoneId.equals("0000"))
				&& userBranchId.equals("0000")) {

			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				// Get those branches that have the same zone id as that of the
				// logged in user's.
				if (zoneId.equals(userZoneId)) {
					String members = userBankId + zoneId + branchId;
					memberIds.add(members);
				}
			}
		}

		// Populate the combo box with all the member ids.
		dynaForm.set("memberIds", memberIds);

		memberIds = null;
		zones = null;
		branches = null;
		allMembers = null;
		mliInfo = null;

		Log.log(Log.INFO, "AdministrationAction", "showmodifyUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to get the users for the chosen Member.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getUsersForMember(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getUsersForMember",
				"Entered");

		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("memberId");

		// The active userIds for the entered memberId is populated from the
		// database.
		ArrayList userIds = admin.getUsers(memberId);

		// Remove the logged in userId and the Admin userId.
		User loggedUser = getUserInformation(request);
		String loggedUserId = loggedUser.getUserId();
		userIds.remove(loggedUserId);
		userIds.remove("ADMIN");
		if (userIds.contains("DEMOUSER")) {
			userIds.remove("DEMOUSER");
		}
		if (userIds.contains("AUDITOR")) {
			userIds.remove("AUDITOR");
		}

		HttpSession session = (HttpSession) request.getSession(false);
		session.setAttribute("MemberSelected", "Y");

		dynaForm.set("activeUsers", userIds);

		userIds = null;

		Log.log(Log.INFO, "AdministrationAction", "getUsersForMember", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to get the users for the chosen Member.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getInactiveUsersForMember(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getInactiveUsersForMember",
				"Entered");

		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("memberId");
		// The active userIds for the entered memberId is populated from the
		// database.
		ArrayList userIds = admin.getDeactivatedUsers(memberId);
		HttpSession session = (HttpSession) request.getSession(false);
		session.setAttribute("MemberSelected", "Y");
		dynaForm.set("deactiveUsers", userIds);

		userIds = null;

		Log.log(Log.INFO, "AdministrationAction", "getInactiveUsersForMember",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for displaying the selected users details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward modifyUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyUser", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		// dynaForm.initialize(mapping);
		Administrator admin = new Administrator();

		// The designation combo-box is populated from the designation master
		// table.
		ArrayList designation = admin.getAllDesignations();
		dynaForm.set("designations", designation);

		// The user details for the selected userId is displayed in the screen.
		String userId = (String) dynaForm.get("userId");
		User selectedUser = admin.getUserInfo(userId);
		BeanUtils.copyProperties(dynaForm, selectedUser);

		// designation=null;
		selectedUser = null;

		Log.log(Log.INFO, "AdministrationAction", "modifyUser", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method adds the modified user details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward afterModifyUser(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterModifyUser", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		User user = new User();
		// String userId=(String)dynaActionForm.get("userId");
		// user.setUserId(userId);
		// System.out.println("First Name :" + dynaActionForm.get("firstName"));
		BeanUtils.populate(user, dynaActionForm.getMap());

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();
		admin.modifyUser(user, createdBy);

		user = null;
		creatingUser = null;
		request.setAttribute("message", "User details modified");
		Log.log(Log.INFO, "AdministrationAction", "afterModifyUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method displays the screen for selecting the user who is to be
	 * deactivated.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showDeactivateUser(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDeactivateUser",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		/*
		 * //Get the logged in user's memberId. User
		 * loggedUser=getUserInformation(request); String
		 * userBankId=loggedUser.getBankId(); String
		 * userZoneId=loggedUser.getZoneId(); String
		 * userBranchId=loggedUser.getBranchId(); String
		 * userMemberId=userBankId+userZoneId+userBranchId;
		 * 
		 * Registration registration=new Registration(); ArrayList memberIds=new
		 * ArrayList(); ArrayList zones=null; ArrayList branches=null; MLIInfo
		 * mliInfo=null; //Add the logged in users member id to the combo box.
		 * memberIds.add(userMemberId);
		 * 
		 * //Get the zones for the MLI to which the logged in user belongs.
		 * zones=registration.getZones(userBankId); int zoneSize=zones.size();
		 * for(int i=0;i<zoneSize;i++){ mliInfo=(MLIInfo)zones.get(i); String
		 * zoneId=mliInfo.getZoneId(); String branchId=mliInfo.getBranchId();
		 * String memberId=userBankId+zoneId+branchId; memberIds.add(memberId);
		 * } //Get the branches for the MLI to which the logged in user belongs.
		 * branches=registration.getAllBranches(userBankId); int
		 * branchSize=branches.size(); for(int i=0;i<branchSize;i++){
		 * mliInfo=(MLIInfo)branches.get(i); String
		 * branchId=mliInfo.getBranchId(); String zoneId=mliInfo.getZoneId();
		 * String memberId=userBankId+zoneId+branchId; memberIds.add(memberId);
		 * }
		 */

		// Get the logged in user's memberId.
		User loggedUser = getUserInformation(request);
		String userBankId = loggedUser.getBankId();
		String userZoneId = loggedUser.getZoneId();
		String userBranchId = loggedUser.getBranchId();
		String userMemberId = userBankId + userZoneId + userBranchId;

		Registration registration = new Registration();
		ArrayList memberIds = new ArrayList();
		ArrayList zones = null;
		ArrayList branches = null;
		ArrayList allMembers = null;
		MLIInfo mliInfo = null;

		// Add the logged in users member id to the combo box.
		memberIds.add(userMemberId);

		if (userBankId.equals("0000") && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			allMembers = registration.getAllMembers();
			int memberSize = allMembers.size();
			for (int i = 0; i < memberSize; i++) {
				mliInfo = (MLIInfo) allMembers.get(i);
				String bankId = mliInfo.getBankId();
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = bankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		}

		else if (!(userBankId.equals("0000")) && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			// Get the zones for the MLI to which the logged in user belongs.
			zones = registration.getZones(userBankId);
			int zoneSize = zones.size();
			for (int i = 0; i < zoneSize; i++) {
				mliInfo = (MLIInfo) zones.get(i);
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		} else if (!(userBankId.equals("0000")) && !(userZoneId.equals("0000"))
				&& userBranchId.equals("0000")) {

			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				// Get those branches that have the same zone id as that of the
				// logged in user's.
				if (zoneId.equals(userZoneId)) {
					String members = userBankId + zoneId + branchId;
					memberIds.add(members);
				}
			}
		}

		// Populate the combo box with all the member ids.
		Administrator admin = new Administrator();
		ArrayList filteredMemIds = new ArrayList();
		for (int i = 0; i < memberIds.size(); i++) {
			String memId = (String) memberIds.get(i);
			if (memId == null) {
				continue;
			}

			String bankId = memId.substring(0, 4);
			String zoneId = memId.substring(4, 8);
			String branchId = memId.substring(8, 12);

			// Fetching the User count of the active users for the Member Id
			int count = admin.getUsersCount(bankId, zoneId, branchId, "A");
			if (count == 0) {
				Log.log(Log.INFO, "AdministrationAction", "showDeactivateUser",
						"Leaving Member Id :" + memId);
				continue;
			} else {
				Log.log(Log.INFO, "AdministrationAction", "showDeactivateUser",
						"Adding Member Id :" + memId);
				filteredMemIds.add(memId);
			}
		}
		dynaForm.set("memberIds", filteredMemIds);

		memberIds = null;
		zones = null;
		branches = null;
		allMembers = null;
		mliInfo = null;

		Log.log(Log.INFO, "AdministrationAction", "showDeactivateUser",
				"Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method deactivates the selected user.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward deactivateUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "deactivateUser", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		String userId = (String) dynaActionForm.get("userId");
		String reason = (String) dynaActionForm.get("reason");

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String deactivatedBy = creatingUser.getUserId();

		admin.deactivateUser(userId, reason, deactivatedBy);

		creatingUser = null;

		request.setAttribute("message", "User Deactivated");

		Log.log(Log.INFO, "AdministrationAction", "deactivateUser", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for displaying the Roles.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showAssignRoles(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showAssignRoles", "Entered");

		AdministrationActionForm adminActionForm = (AdministrationActionForm) form;

		Log.log(Log.INFO, "AdministrationAction", "showAssignRoles", "user Id "
				+ adminActionForm.getUserId());

		Administrator admin = new Administrator();

		ArrayList roles = admin.getAllRoles();
		ArrayList roleNames = new ArrayList();

		for (int i = 0; i < roles.size(); i++) {
			Role role = (Role) roles.get(i);
			if (role == null) {
				continue;
			}
			String roleName = role.getRoleName();
			if ((!roleName.equalsIgnoreCase("DEMOUSER"))
					&& (!roleName.equalsIgnoreCase("AUDITOR"))) {
				// Log.log(Log.INFO,"AdministrationAction","showModifyRole","role.getRoleName() :"
				// + role.getRoleName());
				roleNames.add(roleName);
			}
		}

		adminActionForm.setRoleNames(roleNames);

		roles = null;
		roleNames = null;

		Log.log(Log.INFO, "AdministrationAction", "showAssignRoles", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the screen for modifying the Roles
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showModifyRoles(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showModifyRoles", "Entered");

		AdministrationActionForm adminActionForm = (AdministrationActionForm) form;

		String userId = adminActionForm.getUserId();

		Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles", "userId "
				+ userId);

		Administrator admin = new Administrator();

		ArrayList allRoles = admin.getAllRoles();

		ArrayList roleNames = new ArrayList();

		Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
				"Displaying all roles ");

		for (int i = 0; i < allRoles.size(); i++) {
			Role role = (Role) allRoles.get(i);

			if (role == null) {
				continue;
			}

			String roleName = role.getRoleName();

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
					"roleName " + roleName);

			if ((!roleName.equalsIgnoreCase("DEMOUSER"))
					&& (!roleName.equalsIgnoreCase("AUDITOR"))) {
				// Log.log(Log.INFO,"AdministrationAction","showModifyRole","role.getRoleName() :"
				// + role.getRoleName());
				roleNames.add(roleName);
			}
		}

		adminActionForm.setRoleNames(roleNames);

		// get the user roles
		ArrayList roles = admin.getRoles(userId);

		// get the user privileges.
		ArrayList privileges = admin.getPrivileges(userId);

		Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
				"Displaying all roles ");
		Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
				"setting the user roles ");

		for (int i = 0; i < roles.size(); i++) {
			String role = (String) roles.get(i);

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
					"Displaying all roles ");
			Log.log(Log.DEBUG, "AdministrationAction", "showModifyRoles",
					"role " + role);

			adminActionForm.setRole(role, role);
		}
		Map privilegeMap = new HashMap();

		for (int i = 0; i < privileges.size(); i++) {
			String privilege = (String) privileges.get(i);

			privilegeMap.put(privilege, privilege);
		}

		adminActionForm.setPrivileges(privilegeMap);

		allRoles = null;
		roleNames = null;
		roles = null;
		privileges = null;
		privilegeMap = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyRoles", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the Privileges For Role
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showPrivilegesForRole(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showPrivilegesForRole",
				"Entered");

		AdministrationActionForm adminActionForm = (AdministrationActionForm) form;

		Administrator admin = new Administrator();

		Map roles = adminActionForm.getRoles();

		HttpSession session = request.getSession(false);

		// This variable to hold the roles selected in the user session.
		// This is to remove the privileges for the role if the role is
		// de-selected.
		Map rolesInSession = (Map) session
				.getAttribute(SessionConstants.ROLES_IN_SESSION);

		// get the privileges from the screen.
		Map privileges = adminActionForm.getPrivileges();

		if (rolesInSession == null) {
			// First time. create the map.
			rolesInSession = new HashMap();
			rolesInSession.putAll(roles);
			session.setAttribute(SessionConstants.ROLES_IN_SESSION,
					rolesInSession);
		} else {
			Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
					"Displaying roles in Session");

			// add the currently selected roles also.
			rolesInSession.putAll(roles);

			Set keys = rolesInSession.keySet();
			Iterator iterator = keys.iterator();

			// This variable holds the roles to be removed.
			// ie. de-selected now. But selected in the earlier request.
			ArrayList keysToBeRemoved = new ArrayList();

			while (iterator.hasNext()) {
				Object key = (String) iterator.next();

				Log.log(Log.DEBUG, "AdministrationAction",
						"showPrivilegesForRole", "key " + key);

				if (roles.containsKey(key)) {
					Log.log(Log.DEBUG, "AdministrationAction",
							"showPrivilegesForRole",
							"available in the selected roles...continue");
					// If the roles is part of selection no need to remove
					// privileges for the de-selected roles.

					continue;
				}

				Log.log(Log.DEBUG, "AdministrationAction",
						"showPrivilegesForRole",
						"unavailable ...remove from privileges");

				String value = (String) rolesInSession.get(key);

				keysToBeRemoved.add(key);

				ArrayList rolePrivileges = admin.getPrivilegesForRole(value);

				if (privileges.containsKey("select_all")) {
					// No privileges can be removed. Since the select all
					// privilege check box is checked.
					continue;
				}

				// Remove the privileges for the de-selected role.
				for (int i = 0; i < rolePrivileges.size(); i++) {
					privileges.remove(rolePrivileges.get(i));
				}

			}
			// Remove the key from the session roles...
			for (int i = 0; i < keysToBeRemoved.size(); i++) {
				Object removedKey = rolesInSession.remove(keysToBeRemoved
						.get(i));

				Log.log(Log.DEBUG, "AdministrationAction",
						"showPrivilegesForRole", " removedKey: " + removedKey);
			}
		}

		// This map holds the currently selected roles.
		Map selectedRoles = new HashMap();

		Map selectedPrivileges = new HashMap();

		Set keys = roles.keySet();
		Iterator iterator = keys.iterator();

		ArrayList rolesPrivileges = new ArrayList();

		Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
				"Displaying roles ");

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = (String) roles.get(key);

			Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
					"key and value " + key + " " + value);

			selectedRoles.put(key, value);

			ArrayList rolePrivileges = admin.getPrivilegesForRole(value);

			rolesPrivileges.addAll(rolePrivileges);

		}

		Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
				"displaying selected privileges...");

		Set privilegeKeys = privileges.keySet();
		Iterator privilegeIterator = privilegeKeys.iterator();

		while (privilegeIterator.hasNext()) {
			String key = (String) privilegeIterator.next();
			String value = (String) privileges.get(key);

			Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
					" key and value " + key + " " + value);
			selectedPrivileges.put(key, value);
		}

		Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
				" Assigning role's privileges...");

		for (int i = 0; i < rolesPrivileges.size(); i++) {
			String privilege = (String) rolesPrivileges.get(i);

			Log.log(Log.DEBUG, "AdministrationAction", "showPrivilegesForRole",
					"privilege " + privilege);
			selectedPrivileges.put(privilege, privilege);
		}

		// reset to default values.
		adminActionForm.reset(mapping, request);

		// set the selected roles and their respective privileges.
		adminActionForm.setRoles(selectedRoles);
		adminActionForm.setPrivileges(selectedPrivileges);

		// roles.clear();
		roles = null;

		// rolesInSession.clear();
		rolesInSession = null;

		// privileges.clear();
		privileges = null;

		// keys.clear();
		keys = null;

		// selectedRoles.clear();
		selectedRoles = null;

		// selectedPrivileges.clear();
		selectedPrivileges = null;

		// rolesPrivileges.clear();
		rolesPrivileges = null;

		// privilegeKeys.clear();
		privilegeKeys = null;

		privilegeIterator = null;

		Log.log(Log.INFO, "AdministrationAction", "showPrivilegesForRole",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to assign Roles And Privileges,
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward assignRolesAndPrivileges(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "assignRolesAndPrivileges",
				"Entered");

		AdministrationActionForm adminActionForm = (AdministrationActionForm) form;

		Map roles = adminActionForm.getRoles();

		Map privileges = adminActionForm.getPrivileges();

		ArrayList rolesArray = new ArrayList();

		Set roleKeys = roles.keySet();
		Iterator rolesIterator = roleKeys.iterator();

		while (rolesIterator.hasNext()) {
			rolesArray.add(rolesIterator.next());
		}

		ArrayList privilegesArray = new ArrayList();

		Set privilegeKeys = privileges.keySet();
		Iterator privilegesIterator = privilegeKeys.iterator();

		while (privilegesIterator.hasNext()) {
			privilegesArray.add(privilegesIterator.next());
		}

		// Remove "select all" key.Added by :rp14480

		privilegesArray.remove("select_all");

		Administrator admin = new Administrator();

		String userId = adminActionForm.getUserId();

		Log.log(Log.INFO, "AdministrationAction", "assignRolesAndPrivileges",
				"userId selected " + userId);

		User user = getUserInformation(request);

		admin.assignRolesAndPrivileges(rolesArray, privilegesArray, userId,
				user.getUserId());

		adminActionForm.reset(mapping, request);

		// roles.clear();
		roles = null;

		// privileges.clear();
		privileges = null;

		rolesArray = null;
		rolesArray = null;

		// roleKeys.clear();
		roleKeys = null;

		rolesIterator = null;

		// privilegesArray.clear();
		privilegesArray = null;

		// privilegeKeys.clear();
		privilegeKeys = null;

		privilegesIterator = null;

		user = null;

		request.setAttribute("message",
				"Roles and Privileges assigned for the user");

		Log.log(Log.INFO, "AdministrationAction", "assignRolesAndPrivileges",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to modify Roles and Privileges
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward modifyRolesAndPrivileges(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "modifyRolesAndPrivileges",
				"Entered");

		AdministrationActionForm adminActionForm = (AdministrationActionForm) form;

		Map selectedPrivileges = adminActionForm.getPrivileges();
		Map selectedRoles = adminActionForm.getRoles();

		String userId = adminActionForm.getUserId();

		Log.log(Log.DEBUG, "AdministrationAction", "modifyRolesAndPrivileges",
				"userId " + userId);

		Administrator admin = new Administrator();

		ArrayList roleValues = new ArrayList();

		Set selectedRoleKeys = selectedRoles.keySet();
		Iterator roleIterator = selectedRoleKeys.iterator();

		Log.log(Log.DEBUG, "AdministrationAction", "modifyRolesAndPrivileges",
				"displaying selected roles...");

		while (roleIterator.hasNext()) {
			Object key = roleIterator.next();
			String value = (String) selectedRoles.get(key);

			Log.log(Log.DEBUG, "AdministrationAction",
					"modifyRolesAndPrivileges", "key and values are  " + key
							+ " " + value);

			roleValues.add(value);
		}

		ArrayList privilegeValues = new ArrayList();

		Set selectedPrivilegeKeys = selectedPrivileges.keySet();
		Iterator privilegeIterator = selectedPrivilegeKeys.iterator();

		Log.log(Log.DEBUG, "AdministrationAction", "modifyRolesAndPrivileges",
				"displaying selected Privileges...");

		while (privilegeIterator.hasNext()) {
			Object key = privilegeIterator.next();
			String value = (String) selectedPrivileges.get(key);

			Log.log(Log.DEBUG, "AdministrationAction",
					"modifyRolesAndPrivileges", "key and values are  " + key
							+ " " + value);

			privilegeValues.add(value);

			// Remove select all key from the arraylist. Added by:rp14480
			privilegeValues.remove("select_all");
		}

		User user = getUserInformation(request);

		admin.modifyRolesAndPrivileges(roleValues, privilegeValues, userId,
				user.getUserId());

		// selectedPrivileges.clear();
		selectedPrivileges = null;

		// selectedRoles.clear();
		selectedRoles = null;

		// roleValues.clear();
		roleValues = null;

		// selectedRoleKeys.clear();
		selectedRoleKeys = null;

		roleIterator = null;

		// privilegeValues.clear();
		privilegeValues = null;

		// selectedPrivilegeKeys.clear();
		selectedPrivilegeKeys = null;

		privilegeIterator = null;

		user = null;
		request.setAttribute("message",
				"Roles and Privileges modified for the user");
		Log.log(Log.INFO, "AdministrationAction", "modifyRolesAndPrivileges",
				"Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is for displaying the deactivated users.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showReactivateUser(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "reactivateUser", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		/*
		 * //Get the logged in user's memberId. User
		 * loggedUser=getUserInformation(request); String
		 * userBankId=loggedUser.getBankId(); String
		 * userZoneId=loggedUser.getZoneId(); String
		 * userBranchId=loggedUser.getBranchId(); String
		 * userMemberId=userBankId+userZoneId+userBranchId;
		 * 
		 * Registration registration=new Registration(); ArrayList memberIds=new
		 * ArrayList(); ArrayList zones=null; ArrayList branches=null; MLIInfo
		 * mliInfo=null;
		 * 
		 * //Add the logged in users member id to the combo box.
		 * memberIds.add(userMemberId);
		 * 
		 * //Get the zones for the MLI to which the logged in user belongs.
		 * zones=registration.getZones(userBankId); int zoneSize=zones.size();
		 * for(int i=0;i<zoneSize;i++){ mliInfo=(MLIInfo)zones.get(i); String
		 * zoneId=mliInfo.getZoneId(); String branchId=mliInfo.getBranchId();
		 * String memberId=userBankId+zoneId+branchId; memberIds.add(memberId);
		 * } //Get the branches for the MLI to which the logged in user belongs.
		 * branches=registration.getAllBranches(userBankId); int
		 * branchSize=branches.size(); for(int i=0;i<branchSize;i++){
		 * mliInfo=(MLIInfo)branches.get(i); String
		 * branchId=mliInfo.getBranchId(); String zoneId=mliInfo.getZoneId();
		 * String memberId=userBankId+zoneId+branchId; memberIds.add(memberId);
		 * }
		 */
		// Get the logged in user's memberId.
		User loggedUser = getUserInformation(request);
		String userBankId = loggedUser.getBankId();
		String userZoneId = loggedUser.getZoneId();
		String userBranchId = loggedUser.getBranchId();
		String userMemberId = userBankId + userZoneId + userBranchId;

		Registration registration = new Registration();
		ArrayList memberIds = new ArrayList();
		ArrayList zones = null;
		ArrayList branches = null;
		MLIInfo mliInfo = null;
		ArrayList allMembers = null;

		// Add the logged in users member id to the combo box.
		memberIds.add(userMemberId);

		if (userBankId.equals("0000") && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			allMembers = registration.getAllMembers();
			int memberSize = allMembers.size();
			for (int i = 0; i < memberSize; i++) {
				mliInfo = (MLIInfo) allMembers.get(i);
				String bankId = mliInfo.getBankId();
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = bankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		}

		else if (!(userBankId.equals("0000")) && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			// Get the zones for the MLI to which the logged in user belongs.
			zones = registration.getZones(userBankId);
			int zoneSize = zones.size();
			for (int i = 0; i < zoneSize; i++) {
				mliInfo = (MLIInfo) zones.get(i);
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		} else if (!(userBankId.equals("0000")) && !(userZoneId.equals("0000"))
				&& userBranchId.equals("0000")) {

			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				// Get those branches that have the same zone id as that of the
				// logged in user's.
				if (zoneId.equals(userZoneId)) {
					String members = userBankId + zoneId + branchId;
					memberIds.add(members);
				}
			}
		}

		// Populate the combo box with all the member ids.
		Administrator admin = new Administrator();
		ArrayList filteredMemIds = new ArrayList();
		for (int i = 0; i < memberIds.size(); i++) {
			String memId = (String) memberIds.get(i);
			if (memId == null) {
				continue;
			}

			String bankId = memId.substring(0, 4);
			String zoneId = memId.substring(4, 8);
			String branchId = memId.substring(8, 12);

			// Fetching the User count of the active users for the Member Id
			int count = admin.getUsersCount(bankId, zoneId, branchId, "I");
			if (count == 0) {
				Log.log(Log.INFO, "AdministrationAction", "showDeactivateUser",
						"Leaving Member Id :" + memId);
				continue;
			} else {
				Log.log(Log.INFO, "AdministrationAction", "showReactivateUser",
						"Adding Member Id :" + memId);
				filteredMemIds.add(memId);
			}
		}
		dynaForm.set("memberIds", filteredMemIds);

		memberIds = null;
		zones = null;
		branches = null;
		mliInfo = null;
		allMembers = null;

		Log.log(Log.INFO, "AdministrationAction", "reactivateUser", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method reactivates the chosen deactivated user.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward reactivateUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "reactivateUser", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		String userId = (String) dynaActionForm.get("userId");
		String reason = (String) dynaActionForm.get("reason");

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String reactivatedBy = creatingUser.getUserId();

		admin.reactivateUser(userId, reason, reactivatedBy);

		creatingUser = null;
		request.setAttribute("message", "User Reactivated");
		Log.log(Log.INFO, "AdministrationAction", "reactivateUser", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to change the password.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward changePassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "changePassword", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		PasswordManager passwordMgr = new PasswordManager();

		String oldPassword = (String) dynaActionForm.get("oldPassword");

		String newPassword = (String) dynaActionForm.get("newPassword");

		User user = getUserInformation(request);

		Log.log(Log.DEBUG, "AdministrationAction", "changePassword",
				"User info is " + user);
		/* added by sukumar@path for securitize the user passwords */

		char array1[] = newPassword.toCharArray();
		String invalids = "!@#$%";
		int specialCharactersCount = 0;
		int digitCount = 0;
		int letterCount = 0;
		for (int i = 0; i < invalids.length(); i++) {
			if (newPassword.indexOf(invalids.charAt(i)) >= 0) {
				specialCharactersCount = specialCharactersCount + 1;
				// System.out.println("Given PasswordContains Invalide characters");
				// break;
			}
		}
		// if(array1.length==10){

		for (int j = 0; j < newPassword.length(); j++) {
			if (Character.isLetter(array1[j])) {
				letterCount = letterCount + 1;
			} else if (Character.isDigit(array1[j])) {
				digitCount = digitCount + 1;
			}
		}
		// }
		// System.out.println("letterCount:"+letterCount);
		// System.out.println("digitCount:"+digitCount);
		// System.out.println("specialCharactersCount:"+specialCharactersCount);
		if (digitCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		} else if (specialCharactersCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		} else if (letterCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		}

		/* end part here */

		passwordMgr.changePassword(oldPassword, newPassword, user, null);

		String encryptedPassword = passwordMgr.encryptPassword(newPassword);

		// set the changed password in the user object.
		user.setPassword(encryptedPassword);

		user = null;
		request.setAttribute("message", "Password Changed");

		Log.log(Log.INFO, "AdministrationAction", "changePassword", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to change the password when expired.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward changeExpiredPassword(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "changeExpiredPassword",
				"Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		PasswordManager passwordMgr = new PasswordManager();

		String oldPassword = (String) dynaActionForm.get("oldPassword");

		String newPassword = (String) dynaActionForm.get("newPassword");
		String emailId = (String) dynaActionForm.get("emailId");

		/* added by sukumar@path for securitize the user passwords */

		char array1[] = newPassword.toCharArray();
		String invalids = "!@#$%";
		int specialCharactersCount = 0;
		int digitCount = 0;
		int letterCount = 0;
		for (int i = 0; i < invalids.length(); i++) {
			if (newPassword.indexOf(invalids.charAt(i)) >= 0) {
				specialCharactersCount = specialCharactersCount + 1;
				// System.out.println("Given PasswordContains Invalide characters");
				// break;
			}
		}
		// if(array1.length==10){

		for (int j = 0; j < newPassword.length(); j++) {
			if (Character.isLetter(array1[j])) {
				letterCount = letterCount + 1;
			} else if (Character.isDigit(array1[j])) {
				digitCount = digitCount + 1;
			}
		}
		// }
		// System.out.println("letterCount:"+letterCount);
		// System.out.println("digitCount:"+digitCount);
		// System.out.println("specialCharactersCount:"+specialCharactersCount);
		if (digitCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		} else if (specialCharactersCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		} else if (letterCount == 0) {
			throw new InvalidDataException(
					"Password should comprise of atleast one digit, one special characer and one letter");
		}

		/* end part here */

		String hintQuestion = (String) dynaActionForm.get("newQuestion");

		String hintAnswer = (String) dynaActionForm.get("newAnswer");

		User user = getUserInformation(request);

		Log.log(Log.DEBUG, "AdministrationAction", "changePassword",
				"User info is " + user);

		passwordMgr.changePassword(oldPassword, newPassword, user, emailId);

		String encryptedPassword = passwordMgr.encryptPassword(newPassword);

		// set the changed password in the user object.
		user.setPassword(encryptedPassword);

		String userId = user.getUserId();

		passwordMgr.changeHintQuestionAndAnswer(hintQuestion, hintAnswer,
				userId);

		// set the entered hint question and answer in the user object.
		Hint hint = new Hint();
		hint.setHintQuestion(hintQuestion);
		hint.setHintAnswer(hintAnswer);
		user.setHint(hint);

		user = null;
		hint = null;

		Log.log(Log.INFO, "AdministrationAction", "changeExpiredPassword",
				"Exited");
		return mapping.findForward("showMain");

	}

	/**
	 * This method displays the screen for selecting a user whose password is to
	 * be reset.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showResetPassword(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showResetPassword",
				"Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);
		// Get the logged in user's memberId.
		User loggedUser = getUserInformation(request);
		String userBankId = loggedUser.getBankId();
		String userZoneId = loggedUser.getZoneId();
		String userBranchId = loggedUser.getBranchId();
		String userMemberId = userBankId + userZoneId + userBranchId;

		Registration registration = new Registration();
		ArrayList memberIds = new ArrayList();
		ArrayList zones = null;
		ArrayList branches = null;
		MLIInfo mliInfo = null;
		ArrayList allMembers = null;

		// Add the logged in users member id to the combo box.
		memberIds.add(userMemberId);

		if (userBankId.equals("0000") && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			allMembers = registration.getAllMembers();
			int memberSize = allMembers.size();
			for (int i = 0; i < memberSize; i++) {
				mliInfo = (MLIInfo) allMembers.get(i);
				String bankId = mliInfo.getBankId();
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = bankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		}

		else if (!(userBankId.equals("0000")) && userZoneId.equals("0000")
				&& userBranchId.equals("0000")) {

			// Get the zones for the MLI to which the logged in user belongs.
			zones = registration.getZones(userBankId);
			int zoneSize = zones.size();
			for (int i = 0; i < zoneSize; i++) {
				mliInfo = (MLIInfo) zones.get(i);
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				String memberId = userBankId + zoneId + branchId;
				memberIds.add(memberId);
			}
		} else if (!(userBankId.equals("0000")) && !(userZoneId.equals("0000"))
				&& userBranchId.equals("0000")) {

			// Get the branches for the MLI to which the logged in user belongs.
			branches = registration.getAllBranches(userBankId);
			int branchSize = branches.size();
			for (int i = 0; i < branchSize; i++) {
				mliInfo = (MLIInfo) branches.get(i);
				String branchId = mliInfo.getBranchId();
				String zoneId = mliInfo.getZoneId();
				// Get those branches that have the same zone id as that of the
				// logged in user's.
				if (zoneId.equals(userZoneId)) {
					String members = userBankId + zoneId + branchId;
					memberIds.add(members);
				}
			}
		}

		// Populate the combo box with all the member ids.
		dynaActionForm.set("memberIds", memberIds);

		memberIds = null;
		zones = null;
		branches = null;
		mliInfo = null;
		allMembers = null;

		Log.log(Log.INFO, "AdministrationAction", "showResetPassword", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method resets the password for the chosen user.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward resetPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "resetPassword", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		PasswordManager passwordManager = new PasswordManager();

		// Get the userId whose password is to be reset.
		String userId = (String) dynaActionForm.get("userId");

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String loginUserId = creatingUser.getUserId();

		String userEmailId = passwordManager.resetPassword(userId, loginUserId);
		passwordManager.changeHintQuestionAndAnswer(null, null, userId);

		creatingUser = null;

		request.setAttribute("message", "Password Reset and Mail Send to "
				+ userEmailId);

		Log.log(Log.INFO, "AdministrationAction", "resetPassword", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method displays a screen for entering the new Hint question and
	 * answer. The old hint question and answer is also displayed on the screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showChangeHintQA(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showChangeHintQA", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Get the logged in users old hint question and answer.
		User user = getUserInformation(request);
		Hint hint = user.getHint();
		String oldQuestion = hint.getHintQuestion();
		String oldAnswer = hint.getHintAnswer();

		// Set the old question and answer.
		dynaActionForm.set("newQuestion", oldQuestion);
		dynaActionForm.set("newAnswer", oldAnswer);

		user = null;
		hint = null;

		Log.log(Log.INFO, "AdministrationAction", "showChangeHintQA", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method adds the new Hint Question and Answer to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward changeHintQA(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "changeHintQA", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		PasswordManager passwordManager = new PasswordManager();

		// Retrieve the new question and answer from the screen.
		String newQuestion = (String) dynaActionForm.get("newQuestion");
		String newAnswer = (String) dynaActionForm.get("newAnswer");

		// call the method in the pwd mngr class.
		User user = getUserInformation(request);
		String userId = user.getUserId();
		passwordManager.changeHintQuestionAndAnswer(newQuestion, newAnswer,
				userId);

		// Set the changed hint question and answer in the hint object.
		// Only then it will be reflected in the screen in the same sesion.
		Hint newHint = user.getHint();
		newHint.setHintAnswer(newAnswer);
		newHint.setHintQuestion(newQuestion);

		passwordManager = null;
		user = null;

		request.setAttribute("message", "Hint Question and Answer saved");

		Log.log(Log.INFO, "AdministrationAction", "changeHintQA", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add role.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addRole(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addRole", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		ArrayList privileges = new ArrayList();

		Set keys = Privileges.getKeys();
		Iterator iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String dynaValue = (String) dynaActionForm.get(key);
			if (dynaValue != null && !dynaValue.equals("")) {
				privileges.add(dynaValue);
				// System.out.println("dyna Value is "+dynaValue);
			}
		}

		Log.log(Log.DEBUG, "AdministrationAction", "addRole",
				"After getting the privileges");

		Role role = new Role();

		BeanUtils.populate(role, dynaActionForm.getMap());

		Log.log(Log.DEBUG,
				"AdministrationAction",
				"addRole",
				"Role name and desc are " + role.getRoleName() + " "
						+ role.getRoleDescription());

		role.setPrivileges(privileges);

		Administrator admin = new Administrator();

		User user = getUserInformation(request);

		Log.log(Log.DEBUG, "AdministrationAction", "addRole", "user obj is "
				+ user);

		admin.addRole(role, user);

		// String
		// addApplication=(String)dynaActionForm.get(Privileges.ADD_APPLICATION);

		privileges.clear();
		privileges = null;
		// keys.clear();
		keys = null;
		iterator = null;
		role = null;
		user = null;
		request.setAttribute("message", "Role added");
		Log.log(Log.INFO, "AdministrationAction", "addRole", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to modify Role.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward modifyRole(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyRole", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		Role role = new Role();

		BeanUtils.populate(role, dynaActionForm.getMap());

		Log.log(Log.DEBUG,
				"AdministrationAction",
				"modifyRole",
				"Role name and desc are " + role.getRoleName() + " "
						+ role.getRoleDescription());

		ArrayList privileges = new ArrayList();

		Set keys = Privileges.getKeys();
		Iterator iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String dynaValue = (String) dynaActionForm.get(key);
			if (dynaValue != null && !dynaValue.equals("")) {
				privileges.add(dynaValue);
			}
		}

		role.setPrivileges(privileges);

		User user = getUserInformation(request);

		Administrator admin = new Administrator();

		admin.modifyRole(role, user);

		privileges.clear();
		privileges = null;
		// keys.clear();
		keys = null;
		iterator = null;
		role = null;
		user = null;
		request.setAttribute("message", "Role Modified");
		Log.log(Log.INFO, "AdministrationAction", "modifyRole", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is for assigning a collecting bank to a member.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward assignCB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "assignCB", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		String[] members = (String[]) dynaActionForm.get("memberBank");
		String collectingBank = (String) dynaActionForm.get("collectingBank");

		Log.log(Log.DEBUG, "AdministrationAction", "assignCB",
				"collectingBank  " + collectingBank);

		Registration registration = new Registration();

		int size = members.length;
		boolean areMemsToAssigned = false;
		if (size == 0) {
			areMemsToAssigned = false;
		}
		for (int i = 0; i < size; i++) {

			// Retrieve the member id.
			String member = members[i];
			if ((member == null) || ((member != null) && (member.equals("")))) {
				continue;
			}
			Log.log(Log.DEBUG, "AdministrationAction", "assignCB", "member "
					+ member);
			int start = member.indexOf("(");
			int finish = member.indexOf(")");
			String memberId = member.substring(start + 1, finish);
			Log.log(Log.DEBUG, "AdministrationAction", "assignCB", "memberId"
					+ memberId);

			// Get the logged in user's userId.
			User creatingUser = getUserInformation(request);
			String createdBy = creatingUser.getUserId();
			creatingUser = null;

			registration.assignCollectingBank(memberId, collectingBank,
					createdBy);
			areMemsToAssigned = true;
		}

		if (areMemsToAssigned) {
			request.setAttribute("message",
					"Collecting Bank Assigned for member");
		}
		if (!areMemsToAssigned) {
			request.setAttribute("message",
					"There are no members for which Collecting Bank is to be assigned");
		}
		Log.log(Log.INFO, "AdministrationAction", "assignCB", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method displays the present assigned collecting bank to a member. A
	 * new collecting bank can be assigned from the screen.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public ActionForward modifyCB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyCB", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Get the member entered in the Modify CB screen.
		String memberId = (String) dynaActionForm.get("member");

		// Get the collecting bank object for the member.
		Registration registration = new Registration();
		CollectingBank collectingBankObject = registration
				.getCollectingBank(memberId);

		// Get the collecting bank name and branch .
		String bankName = collectingBankObject.getCollectingBankName();
		String branchName = collectingBankObject.getBranchName();
		String collectingBank = bankName + "," + branchName;

		String cbMessage = "The present collecting bank for " + memberId
				+ " is " + collectingBank;
		dynaActionForm.set("cbMessage", cbMessage);

		// The collecting bank combo-box is populated from the database.
		ArrayList collectingBanks = new ArrayList();
		collectingBanks = registration.getCollectingBanks();

		// Remove the collecting bank already assigned from the collecting bank
		// combo-box.
		collectingBanks.remove(collectingBank);
		dynaActionForm.set("collectingBanks", collectingBanks);

		collectingBanks = null;

		Log.log(Log.INFO, "AdministrationAction", "modifyCB", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward modifyExposureLimits(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyExposureLimits",
				"Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Get the member entered in the Modify CB screen.
		String memberId = (String) dynaActionForm.get("member");
		// System.out.println("memberId:"+memberId);
		String bankId = memberId.substring(1, 5);
		// System.out.println("bankId:"+bankId);
		String bankName = memberId.substring(14);
		// System.out.println("bankName:"+bankName);
		Registration registration = new Registration();
		double presentExposureLimit = registration
				.getPresentExposureLimit(bankId);

		// String
		// cbMessage="The present Exposure Limit for "+memberId+" is "+presentExposureLimit+"(In Cr.)";
		// dynaActionForm.set("cbMessage",cbMessage);
		dynaActionForm.set("bank", bankName);
		String cbMessage = "The present Exposure Limit for " + memberId
				+ " is " + presentExposureLimit + "(In Cr.)";
		dynaActionForm.set("cbMessage", cbMessage);

		Log.log(Log.INFO, "AdministrationAction", "modifyExposureLimits",
				"Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method adds the newly assigned collecting bank for the member into
	 * the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward afterModifyCB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterModifyCB", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		String newCb = (String) dynaActionForm.get("newCollectingBank");
		String member = (String) dynaActionForm.get("member");
		int start = member.indexOf("(");
		int finish = member.indexOf(")");
		String memberId = member.substring(start + 1, finish);

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		Registration registration = new Registration();
		registration.assignCollectingBank(memberId, newCb, createdBy);

		creatingUser = null;
		registration = null;
		request.setAttribute("message", "Collecting Bank modified for member");

		Log.log(Log.INFO, "AdministrationAction", "afterModifyCB", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward afterModifyExposureLimits(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterModifyExposureLimits",
				"Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		double newExposureLimit = ((java.lang.Double) dynaActionForm
				.get("exposureLimit")).doubleValue();

		// System.out.println("New Exposure Limit:"+newExposureLimit);
		String member = (String) dynaActionForm.get("member");
		String bankName = (String) dynaActionForm.get("bank");
		// System.out.println("Bank Name:"+bankName);
		String memberId = member.substring(1, 5);

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		Registration registration = new Registration();
		registration.assignNewExposureLimit(memberId, newExposureLimit);

		creatingUser = null;
		// registration=null;
		request.setAttribute("message",
				"The Exposure Limit has been modified for bank " + bankName
						+ " is " + newExposureLimit + "(In Cr.)");

		Log.log(Log.INFO, "AdministrationAction", "afterModifyExposureLimits",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for deactivating a member.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward deactivateMem(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "deactivateMem", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();

		// Get the details from the screen.
		String memberId = (String) dynaActionForm.get("memberId");
		String reason = (String) dynaActionForm.get("reason");

		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		String memId = bankId + zoneId + branchId;
		if (memId != null && (memId.equals("000000000000"))) {
			throw new InvalidDataException("CGTSI ID can not be deactivated.");
		}

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String deactivatedBy = creatingUser.getUserId();

		// Check if the member is alread deactivated.
		Registration registration = new Registration();
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		String status = mliInfo.getStatus();

		if (status.equals("I")) {
			request.setAttribute("message", "Member Already Deactivated");
		} else {
			administrator.deactivateMember(bankId, zoneId, branchId,
					deactivatedBy, reason);
			request.setAttribute("message", "Member Deactivated");
		}

		creatingUser = null;
		dynaActionForm.initialize(mapping);

		Log.log(Log.INFO, "AdministrationAction", "deactivateMem", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is for reactivating a member.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward reactivateMem(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "reactivateMem", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();

		// Get the details entered in the screen.
		String memberId = (String) dynaActionForm.get("memberId");
		String reason = (String) dynaActionForm.get("reason");

		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		String memId = bankId + zoneId + branchId;
		if (memId != null && (memId.equals("000000000000"))) {
			throw new InvalidDataException("CGTSI ID can not be reactivated.");
		}

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String reactivatedBy = creatingUser.getUserId();

		// Check if the member is alread reactivated.
		Registration registration = new Registration();
		MLIInfo mliInfo = registration.getAllMemberDetails(bankId, zoneId,
				branchId);
		String status = mliInfo.getStatus();

		if (status.equals("A")) {
			request.setAttribute("message", "Member Already Reactivated");
		} else {
			administrator.reactivateMember(bankId, zoneId, branchId,
					reactivatedBy, reason);
			request.setAttribute("message", "Member Reactivated");
		}

		creatingUser = null;
		dynaActionForm.initialize(mapping);

		Log.log(Log.INFO, "AdministrationAction", "reactivateMem", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to display a screen for registering the MLI.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRegisterMLI(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showRegisterMLI", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator admin = new Administrator();
		dynaForm.initialize(mapping);

		// STATES combobox is populated from the state master table.
		ArrayList states = admin.getAllStates();
		dynaForm.set("states", states);

		states = null;

		Log.log(Log.INFO, "AdministrationAction", "showRegisterMLI", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add the MLI details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward registerMLI(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "registerMLI", "Entered");

		// DynaActionForm dynaForm=(DynaActionForm)form;
		Registration registration = new Registration();

		/*
		 * //Register the MLI by sending the screen details to the database
		 * BeanUtils.populate(mliInfo,dynaForm.getMap());
		 * 
		 * dynaForm.initialize(mapping);
		 * 
		 * //Get the logged in user's userId. User
		 * creatingUser=getUserInformation(request); String
		 * createdBy=creatingUser.getUserId();
		 * 
		 * String memberId=registration.registerMLI(mliInfo,createdBy);
		 * 
		 * //Set the bank,zone,branch Ids into the MLIinfo object String
		 * bankId=memberId.substring(0,4); mliInfo.setBankId(bankId);
		 * 
		 * String zoneId=memberId.substring(4,8); mliInfo.setZoneId(zoneId);
		 * 
		 * String branchId=memberId.substring(8,12);
		 * mliInfo.setBranchId(branchId);
		 * 
		 * dynaForm.set("bankId",bankId); dynaForm.set("zoneId",zoneId);
		 * dynaForm.set("branchId",branchId);
		 * 
		 * //Populate the designations combo-box. Administrator
		 * administration=new Administrator(); ArrayList
		 * designations=administration.getAllDesignations();
		 * dynaForm.set("designations",designations);
		 */

		registration = null;
		// designations=null;

		Log.log(Log.INFO, "AdministrationAction", "registerMLI", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to insert the NO details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward afterRegisterMLI(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterRegisterMLI", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Registration registration = new Registration();

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		MLIInfo mliInfo = new MLIInfo();
		// Register the MLI by sending the screen details to the database
		BeanUtils.populate(mliInfo, dynaForm.getMap());
		String memberId = registration.registerMLI(mliInfo, createdBy);

		// Set the bank,zone,branch Ids into the MLIinfo object
		String bankId = memberId.substring(0, 4);
		// mliInfo.setBankId(bankId);

		String zoneId = memberId.substring(4, 8);
		// mliInfo.setZoneId(zoneId);

		String branchId = memberId.substring(8, 12);
		// mliInfo.setBranchId(branchId);

		User NOuser = new User();

		// Populate the user object with details from the screen.
		BeanUtils.populate(NOuser, dynaForm.getMap());

		/*
		 * Since EmailId property exists in both the screens NO email id is
		 * defined as a seperate property.
		 */

		String noEmailId = (String) dynaForm.get("noEmailId");
		NOuser.setEmailId(noEmailId);

		NOuser.setBankId(bankId);
		NOuser.setBranchId(branchId);
		NOuser.setZoneId(zoneId);

		String noUserId = null;
		String message = null;

		try {
			noUserId = registration.createNO(createdBy, NOuser, true);
			message = "memberId is " + memberId + "  NO userId is " + noUserId;
			Log.log(Log.DEBUG, "AdministrationAction", "afterRegisterMLI",
					"noUserId  " + noUserId);
			request.setAttribute("message", message);
		} catch (MailerException mailerException) {
			String errorMessage = mailerException.getMessage();
			message = " memberId is " + memberId;
			request.setAttribute("message", errorMessage + message);
		}

		registration = null;
		creatingUser = null;
		NOuser = null;

		Log.log(Log.INFO, "AdministrationAction", "afterRegisterMLI", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the define org. structure screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDefOrgStr(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDefOrgStruct", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		dynaActionForm.initialize(mapping);

		// Get the logged-in users information.
		User loggedUser = getUserInformation(request);
		String bankId = loggedUser.getBankId();
		String zoneId = loggedUser.getZoneId();
		String branchId = loggedUser.getBranchId();
		if ((bankId.equals(Constants.CGTSI_USER_BANK_ID))
				&& (bankId.equals(Constants.CGTSI_USER_ZONE_ID))
				&& (bankId.equals(Constants.CGTSI_USER_BRANCH_ID))) {
			return mapping.findForward("CGTSIUser");
		} else {

			Administrator admin = new Administrator();
			Registration registration = new Registration();

			// STATES combobox is populated from the state master table.
			ArrayList states = admin.getAllStates();
			dynaActionForm.set("states", states);

			dynaActionForm.set("bankId", bankId);
			dynaActionForm.set("zoneId", zoneId);
			dynaActionForm.set("branchId", branchId);

			// zones combobox is populated from the database.

			MLIInfo mliInfo = new MLIInfo();
			ArrayList reportingZones = new ArrayList();
			ArrayList zonesList = registration.getZones(bankId);
			int size = zonesList.size();

			for (int i = 0; i < size; i++) {
				mliInfo = (MLIInfo) zonesList.get(i);
				String reportingZoneId = mliInfo.getReportingZoneID();
				Log.log(Log.DEBUG, "RegistrationDAO", "showDefOrgStr",
						"reportingZoneId " + reportingZoneId);
				if (reportingZoneId == null || reportingZoneId.equals("")
						|| reportingZoneId.equals("NULL")) {
					String reportingZoneName = mliInfo.getZoneName();
					reportingZones.add(reportingZoneName);
				}
			}
			dynaActionForm.set("reportingZones", reportingZones);

			// dynaActionForm.initialize(mapping);

			admin = null;
			registration = null;
			mliInfo = null;

			states = null;
			reportingZones = null;
			zonesList = null;

			Log.log(Log.INFO, "AdministrationAction", "showDefOrgStruct",
					"Exited");
			return mapping.findForward("MLIUser");
		}
	}

	/**
	 * This method is used to enter the memberId before define org. structure
	 * screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward memberSelected(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "memberSelected", "Entered");
		Registration registration = new Registration();
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Retrieve the memberId entered in the screen.
		String memberId = (String) dynaActionForm.get("memberId");
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		// If an invalid member id is entered filter it.
		registration.getMemberDetails(bankId, zoneId, branchId);

		dynaActionForm.set("bankId", bankId);
		dynaActionForm.set("zoneId", zoneId);
		dynaActionForm.set("branchId", branchId);

		Administrator admin = new Administrator();

		// STATES combobox is populated from the state master table.
		ArrayList states = admin.getAllStates();
		dynaActionForm.set("states", states);

		// zones combobox is populated from the database.

		MLIInfo mliInfo = new MLIInfo();
		ArrayList reportingZones = new ArrayList();

		ArrayList zonesList = registration.getZones(bankId);
		int size = zonesList.size();

		for (int i = 0; i < size; i++) {
			mliInfo = (MLIInfo) zonesList.get(i);
			String reportingZoneId = mliInfo.getReportingZoneID();
			Log.log(Log.DEBUG, "RegistrationDAO", "memberSelected",
					"reportingZoneId " + reportingZoneId);
			if (reportingZoneId == null || reportingZoneId.equals("")
					|| reportingZoneId.equals("NULL")) {
				String reportingZoneName = mliInfo.getZoneName();
				reportingZones.add(reportingZoneName);
			}
		}
		dynaActionForm.set("reportingZones", reportingZones);

		admin = null;
		registration = null;
		mliInfo = null;

		states = null;
		reportingZones = null;
		zonesList = null;

		Log.log(Log.INFO, "AdministrationAction", "memberSelected", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add the details entered in the define org.
	 * structure screen to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward defOrgStr(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "defOrgStruct", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		MLIInfo mliInfo = new MLIInfo();
		Registration registration = new Registration();

		String zoneFromCombo = (String) dynaActionForm.get("zoneList");
		Log.log(Log.DEBUG, "AdministrationAction", "defOrgStr",
				"zoneFromCombo " + zoneFromCombo);

		String zoneFromTextBox = (String) dynaActionForm.get("zoneName");
		Log.log(Log.DEBUG, "AdministrationAction", "defOrgStr",
				"zoneFromTextBox " + zoneFromTextBox);

		BeanUtils.populate(mliInfo, dynaActionForm.getMap());

		if (zoneFromTextBox != null && !zoneFromTextBox.equals("")) {
			mliInfo.setZoneName(zoneFromTextBox);
		} else {
			if (zoneFromCombo != null && !zoneFromCombo.equals("")) {
				mliInfo.setZoneName(zoneFromCombo);
			}
		}

		// Get the logged-in users information.
		User user = getUserInformation(request);

		/*
		 * String userBankId=user.getBankId(); String
		 * userZoneId=user.getZoneId(); String userBranchId=user.getBranchId();
		 */

		String userBankId = (String) dynaActionForm.get("bankId");
		String userZoneId = (String) dynaActionForm.get("zoneId");
		String userBranchId = (String) dynaActionForm.get("branchId");
		System.out.println("User MLI Id:"
				+ userBankId.concat(userZoneId).concat(userBranchId));
		MLIInfo mliDetails = registration.getMemberDetails(userBankId,
				userZoneId, userBranchId);
		String bankName = mliDetails.getBankName();
		System.out.println("Bank Name:" + bankName);
		// Set the bank Id and bank name for the user.
		mliInfo.setBankId(userBankId);
		mliInfo.setBankName(bankName);
		Log.log(Log.DEBUG, "AdministrationAction", "defOrgStr",
				"MCGF Flag is :" + mliDetails.getSupportMCGF());
		mliInfo.setSupportMCGF(mliDetails.getSupportMCGF());

		// Get the logged-in users information.
		String createdBy = user.getUserId();
		String memberId = registration.registerMLI(mliInfo, createdBy);
		System.out.println("Member Id:" + memberId);
		// set to default values.
		dynaActionForm.initialize(mapping);

		// Set the bank,zone,branch Ids into the MLIinfo object
		String bankId = memberId.substring(0, 4);
		mliInfo.setBankId(bankId);
		String zoneId = memberId.substring(4, 8);

		mliInfo.setZoneId(zoneId);
		String branchId = memberId.substring(8, 12);

		mliInfo.setBranchId(branchId);
		dynaActionForm.set("bankId", bankId);
		dynaActionForm.set("zoneId", zoneId);
		dynaActionForm.set("branchId", branchId);

		// Populate the designations combo-box.
		Administrator administration = new Administrator();
		ArrayList designations = administration.getAllDesignations();
		dynaActionForm.set("designations", designations);

		mliInfo = null;
		registration = null;
		user = null;
		mliDetails = null;
		administration = null;
		designations = null;

		Log.log(Log.INFO, "AdministrationAction", "defOrgStruct", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add the user details after defining org.
	 * structure.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward afterDefOrgStr(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterDefOrgStr", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();
		User MLIuser = new User();

		String status = request.getParameter("flag");
		int flagNo = Integer.parseInt(status);

		if (flagNo == 1) {
			// Get the logged-in users information.
			User user = getUserInformation(request);
			String createdBy = user.getUserId();
			BeanUtils.populate(MLIuser, dynaForm.getMap());
			String userEmailId = (String) dynaForm.get("userEmailId");
			MLIuser.setEmailId(userEmailId);

			try {
				administrator.createUser(createdBy, MLIuser, true);
			} catch (MailerException mailerException) {
				request.setAttribute("message",
						"User created successfully. But E-mail could not be sent");
			}

			String bankId = (String) dynaForm.get("bankId");
			String branchId = (String) dynaForm.get("branchId");
			String zoneId = (String) dynaForm.get("zoneId");
			dynaForm.initialize(mapping);
			dynaForm.set("bankId", bankId);
			dynaForm.set("branchId", branchId);
			dynaForm.set("zoneId", zoneId);

			ArrayList designations = administrator.getAllDesignations();
			dynaForm.set("designations", designations);
			designations.clear();
			designations = null;
			Log.log(Log.INFO, "AdministrationAction", "afterDefOrgStr",
					"Exited");
			return mapping.findForward("add another user");// Forwarded to enter
															// user details.

		} else {

			Registration registration = new Registration();

			// STATES combobox is populated from the state master table.
			ArrayList states = administrator.getAllStates();
			dynaForm.set("states", states);

			String bankId = (String) dynaForm.get("bankId");

			/*
			 * //zones combobox is populated from the database. ArrayList
			 * zonesList=registration.getZones(bankId);
			 * dynaForm.set("reportingZones",zonesList);
			 */

			// zones combobox is populated from the database.

			MLIInfo mliInfo = new MLIInfo();
			ArrayList reportingZones = new ArrayList();

			ArrayList zonesList = registration.getZones(bankId);
			int size = zonesList.size();

			for (int i = 0; i < size; i++) {
				mliInfo = (MLIInfo) zonesList.get(i);
				String reportingZoneId = mliInfo.getReportingZoneID();
				Log.log(Log.DEBUG, "RegistrationDAO", "memberSelected",
						"reportingZoneId " + reportingZoneId);
				if (reportingZoneId == null || reportingZoneId.equals("")
						|| reportingZoneId.equals("NULL")) {
					String reportingZoneName = mliInfo.getZoneName();
					reportingZones.add(reportingZoneName);
				}
			}
			Log.log(Log.DEBUG, "AdminAction", "memberSelected",
					"reportingZones" + reportingZones);
			dynaForm.set("reportingZones", reportingZones);

			// Get the logged-in users information.
			User user = getUserInformation(request);
			String createdBy = user.getUserId();

			// Add the user details to the database.
			BeanUtils.populate(MLIuser, dynaForm.getMap());

			String userEmailId = (String) dynaForm.get("userEmailId");
			MLIuser.setEmailId(userEmailId);

			try {
				administrator.createUser(createdBy, MLIuser, true);
			}

			catch (MailerException mailerException) {
				request.setAttribute("message",
						"User created successfully. But E-mail could not be sent");
			}

			user = null;

			Log.log(Log.INFO, "AdministrationAction", "afterDefOrgStr",
					"Exited");
			return mapping.findForward("back to define org str");// Forwarded to
																	// define
																	// org. str.
																	// screen.
		}

	}

	/*
	 * public ActionForward afterMLI( ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception {
	 * 
	 * Log.log(Log.INFO,"AdministrationAction","afterMLI","Entered");
	 * 
	 * DynaActionForm dynaForm=(DynaActionForm)form;
	 * 
	 * Administrator admin=new Administrator(); Registration reg=new
	 * Registration(); User NOuser=new User();
	 * 
	 * String createdBy="PVAIDY01";
	 * BeanUtils.populate(NOuser,dynaForm.getMap()); NOuser.setBankId("0002");
	 * NOuser.setBranchId("0000"); NOuser.setZoneId("0000");
	 * //reg.createNO(createdBy,NOuser);
	 * 
	 * Log.log(Log.INFO,"AdministrationAction","afterMLI","Exited"); return
	 * mapping.findForward("success");
	 * 
	 * }
	 */
	/*
	 * public ActionForward enterAudit( ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception {
	 * 
	 * try { DynaActionForm dynaForm=(DynaActionForm)form;
	 * 
	 * CommonUtility commonUtility=new CommonUtility(); AuditDetails
	 * auditDetails=new AuditDetails();
	 * 
	 * BeanUtils.populate(auditDetails,dynaForm.getMap());
	 * 
	 * String userId="1234"; String remarks=auditDetails.getAuditComments();
	 * System.out.println(remarks);
	 * 
	 * commonUtility.enterAuditDetails(userId,remarks); } catch (Exception
	 * exception) { System.out.println(exception.getMessage()); } return
	 * mapping.findForward("success"); }
	 */

	/**
	 * This method is used to add parameter details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addParameter(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "addParameter", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		ParameterMaster paramMaster = new ParameterMaster();
		Log.log(Log.INFO, "AdministrationAction", "addParameter",
				"Printing DynaActionForm :" + dynaForm);
		BeanUtils.populate(paramMaster, dynaForm.getMap());

		User user = getUserInformation(request);
		Administrator admin = new Administrator();
		String rule = (String) dynaForm.get("rule");
		if (rule.equals("Days")) {
			int days = ((Integer) dynaForm.get("noOfDays")).intValue();
			Log.log(Log.DEBUG, "AdministrationAction", "addParameter",
					"no of days -- " + days);
			paramMaster.setApplicationFilingTimeLimit(days);
		} else if (rule.equals("Periodicity")) {
			int periodicity = ((Integer) dynaForm.get("periodicity"))
					.intValue();
			Log.log(Log.DEBUG, "AdministrationAction", "addParameter",
					"periodicity -- " + periodicity);
			paramMaster.setApplicationFilingTimeLimit(periodicity);
		}
		admin.updateMaster(paramMaster, user.getUserId());

		paramMaster = null;
		user = null;
		admin = null;
		request.setAttribute("message", "Parameter details saved");
		Log.log(Log.INFO, "AdministrationAction", "addParameter", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the parameter screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showParameter(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showParameter", "Entered");

		Administrator admin = new Administrator();

		ParameterMaster parameter = admin.getParameter();

		DynaActionForm dynaForm = (DynaActionForm) form;

		BeanUtils.copyProperties(dynaForm, parameter);

		int applicationFilingTimeLimit = (int) parameter
				.getApplicationFilingTimeLimit();

		if (applicationFilingTimeLimit > 0) {
			dynaForm.set("noOfDays", new Integer(applicationFilingTimeLimit));
			dynaForm.set("rule", "Days");
		} else if (applicationFilingTimeLimit < 0) {
			dynaForm.set("periodicity", new Integer(applicationFilingTimeLimit));
			dynaForm.set("rule", "Periodicity");
		}

		admin = null;

		parameter = null;

		Log.log(Log.INFO, "AdministrationAction", "showParameter", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the inbox.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showInbox(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showInbox", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator admin = new Administrator();

		User user = getUserInformation(request);

		ArrayList mails = admin.getAllAdminMails(user.getUserId());

		dynaForm.set("inboxMails", mails);

		admin = null;

		user = null;

		mails = null;

		Log.log(Log.INFO, "AdministrationAction", "showInbox", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the message.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showMessage", "Entered");

		String mailId = request.getParameter("mailId");

		Administrator admin = new Administrator();

		Message mail = admin.getMailForId(mailId);

		HttpSession session = (HttpSession) request.getSession(false);

		session.setAttribute("adminMail", mail);

		admin = null;

		mail = null;

		Log.log(Log.INFO, "AdministrationAction", "showMessage", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to delete the read mails.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward deleteReadMails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "deleteReadMails", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String selectedMails[] = (String[]) dynaForm.get("checks");

		Log.log(Log.DEBUG, "AdministrationAction", "deleteReadMails",
				"selectedMails " + selectedMails);

		if (selectedMails != null && selectedMails.length > 0) {

			for (int i = 0; i < selectedMails.length; i++) {
				Log.log(Log.DEBUG, "AdministrationAction", "deleteReadMails",
						selectedMails[i]);
			}
			Administrator admin = new Administrator();

			admin.deleteAdminMails(selectedMails);

			admin = null;
		}

		Log.log(Log.INFO, "AdministrationAction", "deleteReadMails", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the send mail screens.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showSendMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "showSendMail", "Entered");

		HttpSession session = (HttpSession) request.getSession(false);

		session.setAttribute("adminMail", null);

		DynaActionForm dynaForm = (DynaActionForm) form;

		dynaForm.initialize(mapping);

		User user = getUserInformation(request);

		String bankId = user.getBankId();

		Registration registration = new Registration();
		ArrayList members = null;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			// CGTSI user.
			members = registration.getAllMembers();
		}

		ArrayList memberIds = new ArrayList();
		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) members.get(i);

				String memberId = mliInfo.getBankId() + mliInfo.getZoneId()
						+ mliInfo.getBranchId();

				memberIds.add(memberId);

				mliInfo = null;

			}
		}

		memberIds
				.add(Constants.CGTSI_USER_BANK_ID
						+ Constants.CGTSI_USER_ZONE_ID
						+ Constants.CGTSI_USER_BRANCH_ID);

		dynaForm.set("members", memberIds);

		memberIds = null;
		members = null;
		user = null;
		registration = null;

		Log.log(Log.INFO, "AdministrationAction", "showSendMail", "Exited");

		return mapping.findForward("success");

	}

	/**
	 * This method is used to get users.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getUsers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getUsers", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("memberId");

		Log.log(Log.DEBUG, "AdministrationAction", "getUsers", "memberId "
				+ memberId);

		Administrator admin = new Administrator();

		ArrayList activeUsers = admin.getAllUsers(memberId);

		ArrayList users = new ArrayList();

		for (int i = 0; i < activeUsers.size(); i++) {
			User user = (User) activeUsers.get(i);

			Log.log(Log.DEBUG, "AdministrationAction", "getUsers", "user id "
					+ user.getUserId());

			users.add(user.getUserId());
		}
		User user = getUserInformation(request);

		users.remove(user.getUserId());

		HttpSession session = (HttpSession) request.getSession(false);
		session.setAttribute("MemberSelected", "Y");

		dynaForm.set("users", users);

		user = null;
		users = null;
		activeUsers = null;

		Log.log(Log.INFO, "AdministrationAction", "getUsers", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to send mail.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward sendMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Message message = new Message();

		BeanUtils.populate(message, dynaForm.getMap());

		User user = getUserInformation(request);

		message.setFrom(user.getUserId());

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail",
				"From " + user.getUserId());

		ArrayList to = new ArrayList();

		to.add(dynaForm.get("userId"));

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail",
				"To " + dynaForm.get("userId"));

		message.setTo(to);

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "from "
				+ message.getFrom());

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "subject "
				+ message.getSubject());
		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "message "
				+ message.getMessage());

		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Entered");

		Administrator admin = new Administrator();
		admin.sendMail(message);
	

		message = null;
		user = null;
		to.clear();
		to = null;
		admin = null;

		request.setAttribute("message", "Mail sent to the user");

		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Exited");

		return mapping.findForward("success");
	}

	public ActionForward replyMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "replyMail", "Entered");
		String bankId = null;
		String zoneId = null;
		String branchId = null;
		String memId = null;
		String userId = null;
		HttpSession session = (HttpSession) request.getSession(false);
		DynaActionForm dynaForm = (DynaActionForm) form;
		Message msg = (Message) session.getAttribute("adminMail");
		if (msg != null) {
			bankId = msg.getBankId();
			zoneId = msg.getZoneId();
			branchId = msg.getBranchId();
			memId = bankId + zoneId + branchId;
			userId = msg.getFrom();
			dynaForm.set("memberId", memId);
			dynaForm.set("userId", userId);
		}
		Log.log(Log.INFO, "AdministrationAction", "replyMail", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to get the districts for a state.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getDistricts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getDistricts", "Entered");
		Administrator admin = new Administrator();
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		String state = (String) dynaActionForm.get("state");

		// Districts combobox is populated from the district master table.
		ArrayList districts = admin.getAllDistricts(state);
		dynaActionForm.set("districts", districts);
		// HttpSession session=request.getSession(false);
		request.setAttribute(SessionConstants.DISTRICT, "1");

		admin = null;
		districts = null;

		Log.log(Log.INFO, "AdministrationAction", "getDistricts", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the assign collecting bank screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showAssignCB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showAssignCB", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		dynaActionForm.initialize(mapping);
		Registration registration = new Registration();
		ArrayList members = registration.getCBUnassignedMembers();

		int size = members.size();
		String[] membersArray = new String[size];

		for (int i = 0; i < size; i++) {
			String member = (String) members.get(i);
			membersArray[i] = member;
		}
		dynaActionForm.set("members", membersArray);

		// The collecting bank combo-box is populated from the database.
		ArrayList collectingBanks = registration.getCollectingBanks();
		dynaActionForm.set("collectingBanks", collectingBanks);

		registration = null;
		members = null;
		collectingBanks = null;

		Log.log(Log.INFO, "AdministrationAction", "showAssignCB", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the modify collecting bank screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showModifyCB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyCB", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);
		Registration registration = new Registration();

		// Populate the combobox with members.
		ArrayList members = registration.getMembersWithCb();
		dynaActionForm.set("cbMembers", members);

		registration = null;
		members = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyCB", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the member banks for modify the exposure
	 * limits
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward showModifyExposureLimit(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyExposureLimit",
				"Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);
		Registration registration = new Registration();

		ArrayList memberNames = registration.getAllHOMembers();
		ArrayList memberDetails = new ArrayList(memberNames.size());

		for (int i = 0; i < memberNames.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) memberNames.get(i);
			String mli = "";
			mli = "(" + mliInfo.getBankId() + mliInfo.getZoneId()
					+ mliInfo.getBranchId() + ")" + mliInfo.getBankName();
			memberDetails.add(mli);
		}
		dynaActionForm.set("cbMembers", memberDetails);

		registration = null;
		memberDetails = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyExposureLimit",
				"Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the register collecting bank screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRegCollBank(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showRegCollBank", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);
		Administrator administrator = new Administrator();

		// Populate the states combobox.
		ArrayList states = administrator.getAllStates();
		dynaActionForm.set("states", states);

		administrator = null;
		states = null;

		Log.log(Log.INFO, "AdministrationAction", "showRegCollBank", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add the collecting bank details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addCollBank(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addCollBank", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Registration registration = new Registration();
		CollectingBank collectingBank = new CollectingBank();

		BeanUtils.populate(collectingBank, dynaForm.getMap());

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		registration.registerCollectingBank(collectingBank, createdBy);

		registration = null;
		collectingBank = null;
		creatingUser = null;

		Log.log(Log.INFO, "AdministrationAction", "addCollBank", "Exited");
		request.setAttribute("message", "Collecting Bank Registered");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the list of collecting banks .
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showModifyCollBank(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyCollBank",
				"Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);

		// The collecting bank combo-box is populated from the database.
		Registration registration = new Registration();
		ArrayList collectingBanks = registration.getCollectingBanks();
		dynaActionForm.set("collectingBanks", collectingBanks);

		registration = null;
		collectingBanks = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyCollBank",
				"Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to display the collecting bank details .
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward modifyCollBank(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyCollBank", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Populate the states combo-box
		Administrator administrator = new Administrator();
		ArrayList states = administrator.getAllStates();
		dynaActionForm.set("states", states);

		String cbName = (String) dynaActionForm.get("collectingBank");
		Registration registration = new Registration();
		int comma = cbName.indexOf(",");
		int length = cbName.length();
		String bankName = cbName.substring(0, comma);
		String branchName = cbName.substring(comma + 1, length);
		CollectingBank collectingBank = registration.viewCollectingBank(
				bankName, branchName);

		dynaActionForm.set("collectingBankName", bankName);
		dynaActionForm.set("branchName", branchName);

		BeanUtils.copyProperties(dynaActionForm, collectingBank);

		administrator = null;
		states = null;
		registration = null;
		collectingBank = null;

		Log.log(Log.INFO, "AdministrationAction", "modifyCollBank", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add the modified collecting bank details to the
	 * database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward afterModifyCollBank(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "afterModifyCollBank",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Registration registration = new Registration();
		CollectingBank collectingBank = new CollectingBank();

		BeanUtils.populate(collectingBank, dynaForm.getMap());

		String cbName = (String) dynaForm.get("collectingBankName");
		String branchName = (String) dynaForm.get("branchName");

		collectingBank.setCollectingBankName(cbName);
		collectingBank.setBranchName(branchName);

		// Get the logged in user's userId.
		User creatingUser = getUserInformation(request);
		String createdBy = creatingUser.getUserId();

		registration.updateCollectingBankDtls(collectingBank, createdBy);

		registration = null;
		collectingBank = null;
		creatingUser = null;

		Log.log(Log.INFO, "AdministrationAction", "afterModifyCollBank",
				"Exited");
		request.setAttribute("message", "Collecting Bank Details Modified");
		return mapping.findForward("success");

	}

	// For update master table screens
	/**
	 * This method is used to add a zone into the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addZone(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addZone", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		ZoneMaster zoneMaster = new ZoneMaster();

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		zoneMaster.setCreatedBy(createdBy);

		BeanUtils.populate(zoneMaster, dynaForm.getMap());
		zoneMaster.updateMaster();

		dynaForm.initialize(mapping);

		zoneMaster = null;
		user = null;

		Log.log(Log.INFO, "AdministrationAction", "addZone", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the region screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRegion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showRegion", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();

		// Populate the zones combo box.
		ArrayList zones = administrator.getAllZones();
		dynaForm.set("zones", zones);

		administrator = null;
		zones = null;

		Log.log(Log.INFO, "AdministrationAction", "showRegion", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add a region for a zone.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addRegion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addRegion", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		RegionMaster regionMaster = new RegionMaster();

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		regionMaster.setCreatedBy(createdBy);

		BeanUtils.populate(regionMaster, dynaForm.getMap());
		regionMaster.updateMaster();

		regionMaster = null;
		user = null;

		Log.log(Log.INFO, "AdministrationAction", "addRegion", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the enter state screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward showState(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showState", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();

		// Populate the regions combobox.
		ArrayList regions = administrator.getAllRegions();
		dynaForm.set("regions", regions);

		administrator = null;
		regions = null;

		Log.log(Log.INFO, "AdministrationAction", "showState", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add a state for a region.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward addState(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addState", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		StateMaster stateMaster = new StateMaster();

		BeanUtils.populate(stateMaster, dynaForm.getMap());

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		stateMaster.setCreatedBy(createdBy);

		stateMaster.updateMaster();

		stateMaster = null;
		user = null;
		request.setAttribute("message", "State added");
		Log.log(Log.INFO, "AdministrationAction", "addState", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the enter district screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDistrict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDistrict", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();
		// Populate the states combo box.
		ArrayList states = administrator.getAllStates();
		dynaForm.set("states", states);

		administrator = null;
		states = null;

		Log.log(Log.INFO, "AdministrationAction", "showDistrict", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add a district for a state.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addDistrict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addDistrict", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		DistrictMaster districtMaster = new DistrictMaster();
		BeanUtils.populate(districtMaster, dynaForm.getMap());

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		districtMaster.setCreatedBy(createdBy);

		districtMaster.updateMaster();

		districtMaster = null;
		user = null;
		request.setAttribute("message", "District added");
		Log.log(Log.INFO, "AdministrationAction", "addDistrict", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the enter designation screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDesignation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDesignation", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();

		// Populate the designations combo box.
		ArrayList designations = administrator.getAllDesignations();
		dynaForm.set("designations", designations);

		administrator = null;
		designations = null;

		Log.log(Log.INFO, "AdministrationAction", "showDesignation", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add a designation to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addDesignation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addDesignation", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		DesignationMaster designationMaster = new DesignationMaster();
		BeanUtils.populate(designationMaster, dynaForm.getMap());
		designationMaster.updateMaster();

		designationMaster = null;
		request.setAttribute("message", "Designation details saved");
		Log.log(Log.INFO, "AdministrationAction", "addDesignation", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to get the description for a designation .
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getDesigDescr(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getDesigDescr", "Entered");
		Administrator admin = new Administrator();
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		String designation = (String) dynaActionForm.get("desigName");
		// Get the description for the designation entered.
		String desigDescr = admin.getDesigDescr(designation);
		dynaActionForm.set("desigDesc", desigDescr);

		admin = null;
		Log.log(Log.INFO, "AdministrationAction", "getDesigDescr", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the PLR screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showPLR(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showPLR", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Registration registration = new Registration();

		// Populate the banks combo box from the database.

		ArrayList mliInfo = registration.getAllMLIs();
		ArrayList plrBanks = new ArrayList();
		int size = mliInfo.size();
		for (int i = 0; i < size; i++) {
			MLIInfo mliDetail = (MLIInfo) mliInfo.get(i);
			// String bankName=mliDetail.getBankName();

			String bankId = mliDetail.getBankId();
			String zoneId = mliDetail.getZoneId();
			String branchId = mliDetail.getBranchId();
			String shortName = mliDetail.getShortName();

			// String concatString = shortName + " " + bankId + zoneId +
			// branchId;
			String concatString = "(" + bankId + zoneId + branchId + ")"
					+ shortName;

			plrBanks.add(concatString);
		}

		dynaForm.set("plrBanks", plrBanks);

		plrBanks = null;

		Log.log(Log.INFO, "AdministrationAction", "showPLR", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add the PLR details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addPLR(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addPLR", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		PLRMaster plrMaster = new PLRMaster();
		BeanUtils.populate(plrMaster, dynaForm.getMap());

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		plrMaster.setCreatedBy(createdBy);

		plrMaster.updateMaster();

		plrMaster = null;
		user = null;
		request.setAttribute("message", "PLR details saved");
		Log.log(Log.INFO, "AdministrationAction", "addPLR", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the Modify PLR Filter screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showModifyPLRFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyPLRFilter",
				"Entered");
		AdministrationActionForm dynaForm = (AdministrationActionForm) form;
		// dynaForm.initialize(mapping);
		Registration registration = new Registration();

		// Populate the banks combo box from the database.

		ArrayList mliInfo = registration.getAllMLIs();
		ArrayList plrBanks = new ArrayList();
		int size = mliInfo.size();
		for (int i = 0; i < size; i++) {
			MLIInfo mliDetail = (MLIInfo) mliInfo.get(i);
			// String bankName=mliDetail.getBankName();

			String bankId = mliDetail.getBankId();
			String zoneId = mliDetail.getZoneId();
			String branchId = mliDetail.getBranchId();
			String shortName = mliDetail.getShortName();

			// String concatString = shortName + " " + bankId + zoneId +
			// branchId;
			String concatString = "(" + bankId + zoneId + branchId + ")"
					+ shortName;

			plrBanks.add(concatString);
		}

		dynaForm.setPlrBanks(plrBanks);
		dynaForm.setShortNameMemId("");

		plrBanks = null;

		Log.log(Log.INFO, "AdministrationAction", "showModifyPLRFilter",
				"Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the Modify PLR Filter screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getPLRSummary(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getPLRSummary", "Entered");
		Administrator administrator = new Administrator();
		AdministrationActionForm dynaActionForm = (AdministrationActionForm) form;
		Registration registration = new Registration();

		// Get the Short Name and Member Id for which plr details is to be
		// retrieved.
		String shortNameMemberId = (String) dynaActionForm.getShortNameMemId();
		String memberId = null;
		/*
		 * StringTokenizer tokenizer = new
		 * StringTokenizer(shortNameMemberId,")"); Vector tokens = new Vector();
		 * String nextToken = null; String memberId = null; String memberIdToken
		 * = null; while(tokenizer.hasMoreElements()) { nextToken =
		 * (String)tokenizer.nextElement();
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRSummary","**************");
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRSummary","Next Token :" + nextToken);
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRSummary","**************"); tokens.addElement(nextToken); }
		 * if((tokens != null) && (tokens.size() > 0)) { memberIdToken =
		 * (String)tokens.elementAt(0); } else {
		 * Log.log(Log.ERROR,"AdministrationAction"
		 * ,"getPLRSummary","PLR Details could not be fetched for the member.");
		 * } if(memberIdToken != null) { memberId =
		 * memberIdToken.substring(1,memberIdToken.length()); } else {
		 * Log.log(Log.DEBUG,"AdministrationAction","getPLRSummary()",
		 * "Member Id could not be parsed."); }
		 */
		memberId = shortNameMemberId.substring(1, 13);
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails", "bankId :"
				+ bankId);
		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails", "zoneId :"
				+ zoneId);
		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
				"branchId :" + branchId);

		// Fetching the Member Details

		MLIInfo mliInfo = (MLIInfo) registration.getMemberDetails(bankId,
				zoneId, branchId);

		String bankName = mliInfo.getBankName();
		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
				"bankName :" + bankName);

		dynaActionForm.setMemberId(bankId + zoneId + branchId);
		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
				"memberId :" + bankId + zoneId + branchId);

		ArrayList plrMasters = administrator.getPlrDetails(bankId);

		for (int i = 0; i < plrMasters.size(); i++) {
			PLRMaster plrMaster = (PLRMaster) plrMasters.get(i);
			CustomisedDate custome = new CustomisedDate();
			custome.setDate(plrMaster.getStartDate());
			plrMaster.setStartDate(custome);

			custome = new CustomisedDate();
			custome.setDate(plrMaster.getEndDate());
			plrMaster.setEndDate(custome);
		}

		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
				"plrMasters :" + plrMasters);

		// String bankName=plrMaster.getBankName();
		dynaActionForm.setPlrDetails(plrMasters);
		/*
		 * String keyString = "key-"; for(int i=0; i<plrMasters.size(); i++) {
		 * PLRMaster plrObj = (PLRMaster)plrMasters.get(i); if(plrObj == null) {
		 * continue; } java.util.Date startDate = plrObj.getStartDate();
		 * java.util.Date endDate = plrObj.getEndDate(); CustomisedDate
		 * customStartDate=new CustomisedDate(); CustomisedDate
		 * customEndDate=new CustomisedDate();
		 * customStartDate.setDate(startDate); customEndDate.setDate(endDate);
		 * plrObj.setStartDate(customStartDate);
		 * plrObj.setEndDate(customEndDate);
		 * //dynaActionForm.setPlrMasters(keyString+i,plrObj); }
		 */
		Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails", "bankName "
				+ bankName);
		dynaActionForm.setBankName(bankName);

		Log.log(Log.INFO, "AdministrationAction", "getPLRDetails", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the Modify PLR Filter screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getPLRDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getPLRDetails", "Entered");
		Administrator administrator = new Administrator();
		AdministrationActionForm dynaActionForm = (AdministrationActionForm) form;

		ArrayList plrMasters = dynaActionForm.getPlrDetails();
		String plrIdStr = request.getParameter("plrId");
		int plrId = 0;

		if (plrIdStr != null && !plrIdStr.equals("")) {
			plrId = Integer.parseInt(plrIdStr);
		}
		dynaActionForm.setPlrIndexValue(plrId);

		for (int i = 0; i < plrMasters.size(); i++) {
			PLRMaster plrObj = (PLRMaster) plrMasters.get(i);

			Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
					"start date " + plrObj.getStartDate());
			Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
					"start date " + plrObj.getEndDate());
			Log.log(Log.DEBUG, "AdministrationAction", "getPLRDetails",
					"start date " + plrObj.getPlrId());

			if (plrId == plrObj.getPlrId()) {
				dynaActionForm.setPlrMaster(plrObj);
				break;
			}
		}
		/*
		 * Registration registration = new Registration();
		 * 
		 * //Get the Short Name and Member Id for which plr details is to be
		 * retrieved. String
		 * shortNameMemberId=(String)dynaActionForm.getShortNameMemId();
		 * StringTokenizer tokenizer = new
		 * StringTokenizer(shortNameMemberId,")"); Vector tokens = new Vector();
		 * String nextToken = null; String memberId = null; String memberIdToken
		 * = null; while(tokenizer.hasMoreElements()) { nextToken =
		 * (String)tokenizer.nextElement();
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRDetails","**************");
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRDetails","Next Token :" + nextToken);
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRDetails","**************"); tokens.addElement(nextToken); }
		 * if((tokens != null) && (tokens.size() > 0)) { memberIdToken =
		 * (String)tokens.elementAt(0); } else {
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"getPLRDetails","PLR Details could not be fetched for the member.");
		 * } if(memberIdToken != null) { memberId =
		 * memberIdToken.substring(1,memberIdToken.length()); } else {
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails()",
		 * "Member Id could not be parsed."); } String bankId =
		 * memberId.substring(0,4); String zoneId = memberId.substring(4,8);
		 * String branchId = memberId.substring(8,12);
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails","bankId :" +
		 * bankId);
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails","zoneId :" +
		 * zoneId);
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails","branchId :"
		 * + branchId);
		 * 
		 * // Fetching the Member Details
		 * 
		 * MLIInfo mliInfo =
		 * (MLIInfo)registration.getMemberDetails(bankId,zoneId,branchId);
		 * 
		 * String bankName = mliInfo.getBankName();
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails","bankName :"
		 * + bankName);
		 * 
		 * dynaActionForm.setMemberId(bankId + zoneId + branchId);
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails","memberId :"
		 * + bankId + zoneId + branchId);
		 * 
		 * ArrayList plrMasters= administrator.getPlrDetails(bankId); // String
		 * bankName=plrMaster.getBankName();
		 * dynaActionForm.setPlrDetails(plrMasters); String keyString = "key-";
		 * for(int i=0; i<plrMasters.size(); i++) { PLRMaster plrObj =
		 * (PLRMaster)plrMasters.get(i); if(plrObj == null) { continue; }
		 * java.util.Date startDate = plrObj.getStartDate(); java.util.Date
		 * endDate = plrObj.getEndDate(); CustomisedDate customStartDate=new
		 * CustomisedDate(); CustomisedDate customEndDate=new CustomisedDate();
		 * customStartDate.setDate(startDate); customEndDate.setDate(endDate);
		 * plrObj.setStartDate(customStartDate);
		 * plrObj.setEndDate(customEndDate);
		 * dynaActionForm.setPlrMasters(keyString+i,plrObj); }
		 * Log.log(Log.DEBUG,
		 * "AdministrationAction","getPLRDetails","bankName "+bankName);
		 * dynaActionForm.setBankName(bankName);
		 */

		Log.log(Log.INFO, "AdministrationAction", "getPLRDetails", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to get hte PLR details for a bank.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	/*
	 * public ActionForward getPlrBankDetails( ActionMapping mapping, ActionForm
	 * form, HttpServletRequest request, HttpServletResponse response) throws
	 * Exception {
	 * 
	 * Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails","Entered");
	 * 
	 * Administrator administrator=new Administrator(); DynaActionForm
	 * dynaActionForm=(DynaActionForm)form;
	 * 
	 * //Get the bank for which plr details is to be retrieved. String
	 * shortNameMemberId=(String)dynaActionForm.get("shortNameMemId");
	 * StringTokenizer tokenizer = new StringTokenizer(shortNameMemberId," ");
	 * Vector tokens = new Vector(); String nextToken = null; String memberId =
	 * null; while(tokenizer.hasMoreElements()) { nextToken =
	 * (String)tokenizer.nextElement();
	 * Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails"
	 * ,"**************");
	 * Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails"
	 * ,"Next Token :" + nextToken);
	 * Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails"
	 * ,"**************"); tokens.addElement(nextToken); } if((tokens != null)
	 * && (tokens.size() > 0)) { memberId = (String)tokens.elementAt(1); } else
	 * { Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails",
	 * "PLR Details could not be fetched for the member."); } String bankId =
	 * memberId.substring(0,4); String zoneId = memberId.substring(4,8); String
	 * branchId = memberId.substring(8,12);
	 * 
	 * PLRMaster plrMaster= administrator.getPlrDetails(bankId); String
	 * bankName=plrMaster.getBankName();
	 * Log.log(Log.DEBUG,"AdministrationAction"
	 * ,"getPlrBankDetails","bankName "+bankName);
	 * BeanUtils.copyProperties(dynaActionForm, plrMaster);
	 * 
	 * administrator=null;
	 * 
	 * Log.log(Log.INFO,"AdministrationAction","getPlrBankDetails","Exited");
	 * return mapping.findForward("success");
	 * 
	 * }
	 */
	public ActionForward modifyPLR(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "modifyPLR", "Entered");
		AdministrationActionForm dynaForm = (AdministrationActionForm) form;
		Administrator admin = new Administrator();
		PLRMaster plrMaster = dynaForm.getPlrMaster();

		Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR", "Dyna Form id "
				+ plrMaster.getPlrId());
		Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR",
				"Dyna Form: start date " + plrMaster.getStartDate());
		Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR",
				"Dyna Form: end date " + plrMaster.getEndDate());

		/*
		 * PLRMaster plrMaster=new PLRMaster();
		 * 
		 * String startDateStr=request.getParameter("startDate"); String
		 * endDateStr=request.getParameter("endDate"); String shortTermPLR;
		 * String mediumTermPLR; String longTermPLR; String shortTermPeriod;
		 * String mediumTermPeriod; Strn longTermPeriod; private String
		 * createdBy; private String PLR; //Type of PLR. (rp14480) private
		 * double BPLR; //Bench Mark PLR. (rp14480) private String bankId;
		 * private int plrId=0;
		 * 
		 * SimpleDateFormat simpleFormat=new SimpleDateFormat("dd/MM/yyyy");
		 * if(startDateStr!=null && !startDateStr.equals("")) { Date
		 * startDate=simpleFormat.parse(startDateStr,new ParsePosition(0));
		 * plrMaster.setStartDate(startDate); } if(endDateStr!=null &&
		 * !endDateStr.equals("")) { Date
		 * endDate=simpleFormat.parse(endDateStr,new ParsePosition(0));
		 * plrMaster.setEndDate(endDate); }
		 * plrMaster.setShortTermPLR(Double.parseDouble
		 * (request.getParameter("shortTermPLR"))); plrMaster.
		 * 
		 * System.out.println();
		 * System.out.println(request.getParameter("endDate"));
		 * System.out.println(request.getParameter("shortTermPLR"));
		 */

		int plrId = dynaForm.getPlrIndexValue();

		Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR", "plrId "
				+ plrId);

		/*
		 * ArrayList plrDetails=dynaForm.getPlrDetails(); PLRMaster plr=null;
		 * 
		 * for(int i=0;i<plrDetails.size();i++) {
		 * plr=(PLRMaster)plrDetails.get(i);
		 * 
		 * Log.log(Log.DEBUG,"AdministrationAction","modifyPLR","ArrayList id "+plr
		 * .getPlrId());
		 * Log.log(Log.DEBUG,"AdministrationAction","modifyPLR","start date "
		 * +plr.getStartDate());
		 * Log.log(Log.DEBUG,"AdministrationAction","modifyPLR"
		 * ,"end date "+plr.getEndDate());
		 * 
		 * if(plr.getPlrId()==plrId) { break; } }
		 */

		/*
		 * String shortNameMemberId=(String)dynaForm.getShortNameMemId();
		 * StringTokenizer tokenizer = new
		 * StringTokenizer(shortNameMemberId,")"); Vector tokens = new Vector();
		 * String nextToken = null; String memberId = null; String memberIdToken
		 * = null; while(tokenizer.hasMoreElements()) { nextToken =
		 * (String)tokenizer.nextElement();
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"modifyPLR","**************");
		 * Log.log(Log.INFO,"AdministrationAction","modifyPLR","Next Token :" +
		 * nextToken);
		 * Log.log(Log.INFO,"AdministrationAction","modifyPLR","**************"
		 * ); tokens.addElement(nextToken); } if((tokens != null) &&
		 * (tokens.size() > 0)) { memberIdToken = (String)tokens.elementAt(0); }
		 * else { Log.log(Log.INFO,"AdministrationAction","modifyPLR",
		 * "PLR Details could not be fetched for the member."); }
		 * if(memberIdToken != null) { memberId =
		 * memberIdToken.substring(1,memberIdToken.length()); } else {
		 * Log.log(Log.INFO,"AdministrationAction","getPLRDetails()",
		 * "Member Id could not be parsed."); } String bankId =
		 * memberId.substring(0,4); // String zoneId = memberId.substring(4,8);
		 * // String branchId = memberId.substring(8,12); // String keyString =
		 * "key-"; Map plrDetails = dynaForm.getPlrMasters();
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"modifyPLR","Printing the HashMap :" + plrDetails); Administrator
		 * admin = new Administrator(); ArrayList plrDetailsArray = new
		 * ArrayList(); Log.log(Log.INFO,"AdministrationAction","modifyPLR",
		 * "************************************");
		 * Log.log(Log.INFO,"AdministrationAction"
		 * ,"modifyPLR","Size of the Map :" + plrDetails.size());
		 * Log.log(Log.INFO,"AdministrationAction","modifyPLR",
		 * "************************************"); Set plrDetailsSet =
		 * plrDetails.keySet(); Iterator plrDetailsIterator =
		 * plrDetailsSet.iterator(); while(plrDetailsIterator.hasNext()) { //
		 * PLRMaster object = (PLRMaster)plrDetailsIterator.next(); String key =
		 * (String)plrDetailsIterator.next(); PLRMaster object =
		 * (PLRMaster)plrDetails.get(key);
		 * Log.log(Log.INFO,"AdministrationAction","modifyPLR","Object is :" +
		 * object);
		 * 
		 * if(!plrDetailsArray.contains(object)) { plrDetailsArray.add(object);
		 * } }
		 */
		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();

		if (plrMaster != null) {
			ArrayList tempList = dynaForm.getPlrDetails();

			Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR",
					" before size " + dynaForm.getPlrDetails().size());

			for (int i = 0; i < tempList.size(); i++) {
				PLRMaster plrMasterTemp = (PLRMaster) tempList.get(i);
				if (plrMasterTemp.getPlrId() == plrId) {
					tempList.remove(i);
					--i;
				}

			}

			Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR",
					" after size " + dynaForm.getPlrDetails().size());

			// boolean
			// isValidDates=admin.validatePLRDetails(plrMaster,dynaForm.getPlrDetails(),createdBy);
			boolean isValidDates = admin.validatePLRDetails(plrMaster,
					tempList, createdBy);

			Log.log(Log.DEBUG, "AdministrationAction", "modifyPLR",
					" isValidDates " + isValidDates);

			if (isValidDates) {
				admin.modifyPLRMaster(plrMaster, createdBy);
				request.setAttribute("message", "PLR details saved");
			} else {
				request.setAttribute("message",
						"PLR details already exists for this period");
			}
		} else {
			Log.log(Log.ERROR, "AdministrationAction", "modifyPLR",
					"Unable to get PLR object");
		}

		user = null;

		Log.log(Log.INFO, "AdministrationAction", "modifyPLR", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the enter alert message screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showAlertMessages(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showAlertMessages",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();
		dynaForm.initialize(mapping);
		// Populate the alert messages combo box from the database.
		ArrayList alerts = administrator.getAlertTitles();
		dynaForm.set("alertTitles", alerts);

		administrator = null;
		alerts = null;

		Log.log(Log.INFO, "AdministrationAction", "showAlertMessages", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add an alert message to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addAlertMessages(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addAlertMessages", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		AlertMaster alertMaster = new AlertMaster();
		BeanUtils.populate(alertMaster, dynaForm.getMap());
		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		alertMaster.setCreatedBy(createdBy);

		alertMaster.updateMaster();

		alertMaster = null;
		user = null;
		request.setAttribute("message", "Alert Message added");
		Log.log(Log.INFO, "AdministrationAction", "addAlertMessages", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to get the alert message.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getAlertMessage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getAlertMessage", "Entered");
		Administrator administrator = new Administrator();
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Get the message associated with the alert title.
		String alertTitle = (String) dynaActionForm.get("alertTitle");
		String alertMessage = administrator.getAlertMessage(alertTitle);
		dynaActionForm.set("alertMessage", alertMessage);

		administrator = null;

		Log.log(Log.INFO, "AdministrationAction", "getAlertMessage", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the enter exception message screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showExceptionMessages(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showExceptionMessages",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();
		dynaForm.initialize(mapping);

		// Populate the exception titles combo box from the database.
		ArrayList exceptions = administrator.getExceptionTitles();
		dynaForm.set("exceptionTitles", exceptions);

		administrator = null;
		exceptions = null;

		Log.log(Log.INFO, "AdministrationAction", "showExceptionMessages",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to get the exception message.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getExceptionMessage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getExceptionMessage",
				"Entered");
		Administrator administrator = new Administrator();
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		ExceptionMaster exceptionMaster = new ExceptionMaster();

		// Get the message and Type associated with the exception title.
		String exceptionTitle = (String) dynaActionForm.get("exceptionTitle");
		exceptionMaster = administrator.getExceptionMessage(exceptionTitle);
		dynaActionForm.set("exceptionMessage",
				exceptionMaster.getExceptionMessage());
		dynaActionForm.set("exceptionType", exceptionMaster.getExceptionType()
				.trim());

		exceptionMaster = null;

		Log.log(Log.INFO, "AdministrationAction", "getExceptionMessage",
				"Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add an exception message to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addExceptionMessages(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addExceptionMessages",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ExceptionMaster exceptionMaster = new ExceptionMaster();
		BeanUtils.populate(exceptionMaster, dynaForm.getMap());
		// add the exception message to the database.
		exceptionMaster.updateMaster();
		exceptionMaster = null;
		Log.log(Log.INFO, "AdministrationAction", "addExceptionMessages",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add the Audit details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addEnterAudit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addEnterAudit", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		CommonUtility commonUtility = new CommonUtility();
		String remarks = (String) dynaForm.get("auditComments");
		String CGPAN = (String) dynaForm.get("cgpan");

		CGPAN = CGPAN.trim();

		Log.log(Log.INFO, "AdministrationAction", "addEnterAudit", "CGPAN :"
				+ CGPAN);
		if ((CGPAN == null) || ((CGPAN != null) && (CGPAN.equals("")))) {
			throw new NoDataException("Please enter a valid CGPAN.");
		}
		Administrator admin = new Administrator();
		ArrayList cgpans = admin.getAllCgpans();
		boolean isCGPANInTheSystem = false;
		for (int i = 0; i < cgpans.size(); i++) {
			String pan = (String) cgpans.get(i);
			if (pan == null) {
				continue;
			}
			if (CGPAN.equals(pan)) {
				isCGPANInTheSystem = true;
			}

		}
		if (!isCGPANInTheSystem) {
			throw new NoDataException(
					"The CGPAN does not exist in the Database or may have been expired.\n Please enter a valid CGPAN");
		}

		// Get the logged-in users information.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		commonUtility.enterAuditDetails(CGPAN, createdBy, remarks);

		commonUtility = null;
		user = null;
		request.setAttribute("message", "Audit comments saved");
		Log.log(Log.INFO, "AdministrationAction", "addEnterAudit", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the review audit details screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showReviewAudit(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showReviewAudit", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		CommonUtility commonUtility = new CommonUtility();
		AuditDetails auditDetails = new AuditDetails();

		dynaForm.initialize(mapping);

		// Pass the Audit Id as -1 which will fetch the latest Audit details.
		auditDetails.setAuditId(Constants.AUDIT_ID);
		auditDetails = commonUtility.viewAuditDetails(auditDetails);

		// Retrieve the audit comment and Id and set it in the dynaForm.
		String message = auditDetails.getMessage();

		/*
		 * In audit details class audit id is int.But to set it as a string to
		 * dynaForm concatenate it with a blank string.
		 */

		String auditId = ("" + auditDetails.getAuditId());

		/*
		 * To view Audit Details from Claims Processing Module
		 */
		HttpSession session = (HttpSession) request.getSession(false);
		// System.out.println("Main Menu :" + session.getAttribute("mainMenu"));
		if ((session.getAttribute("mainMenu")).equals(MenuOptions_back
				.getMenu(MenuOptions_back.CP_SETTLEMENT))) {
			String memId = request.getParameter("MemberId");
			dynaForm.set("memberId", memId);
			// System.out.println("Member Id from Reg Action:" +
			// dynaForm.get("memberId"));
		}

		dynaForm.set("auditId", auditId);
		dynaForm.set("comment", message);

		commonUtility = null;
		auditDetails = null;

		Log.log(Log.INFO, "AdministrationAction", "showReviewAudit", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to add the review details of the audit .
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addReviewAudit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showReviewAudit", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		CommonUtility commonUtility = new CommonUtility();

		// Get the review comment from the dynaForm.
		String reviewedComment = (String) dynaForm.get("reviewedComments");
		// Get the logged-in users information.
		User user = getUserInformation(request);
		String userId = user.getUserId();
		// Get the audit Id from DynaForm
		int auditId = Integer.parseInt((String) dynaForm.get("auditId"));
		// Get the CGPAN from DynaForm
		String CGPAN = ((String) dynaForm.get("cgpan"));

		commonUtility
				.updateAuditReview(CGPAN, auditId, userId, reviewedComment);

		commonUtility = null;

		request.setAttribute("message", "Review details saved");

		Log.log(Log.INFO, "AdministrationAction", "showReviewAudit", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the next audit detail for review.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showReviewAuditNext(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showReviewAuditNext",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		// Clear the CGPAN text field.
		dynaForm.set("cgpan", "");
		CommonUtility commonUtility = new CommonUtility();
		AuditDetails auditDetails = new AuditDetails();

		String previousAuditId = ("" + (String) dynaForm.get("auditId"));
		int previousId = Integer.parseInt(previousAuditId);
		auditDetails.setAuditId(previousId);

		auditDetails = commonUtility.viewAuditDetails(auditDetails);

		// Retrieve the audit comment and Id and set it in the dynaForm.
		String message = auditDetails.getMessage();
		String auditId = ("" + auditDetails.getAuditId());

		dynaForm.set("auditId", auditId);
		dynaForm.set("comment", message);

		commonUtility = null;
		auditDetails = null;

		Log.log(Log.INFO, "AdministrationAction", "showReviewAuditNext",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to show the previous audit details for review.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showReviewAuditPrev(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showReviewAuditPrev",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		// Clear the CGPAN text field.
		dynaForm.set("cgpan", "");

		CommonUtility commonUtility = new CommonUtility();
		AuditDetails auditDetails = new AuditDetails();

		String previousAuditId = ("" + (String) dynaForm.get("auditId"));
		Log.log(Log.DEBUG, "AdministrationAction", "showReviewAuditPrevious",
				"previousAuditId " + previousAuditId);
		int previousId = Integer.parseInt(previousAuditId);
		auditDetails.setAuditId(previousId);

		auditDetails = commonUtility.viewRevAuditDetails(auditDetails);

		// Retrieve the audit comment and Id and set it in the dynaForm.
		String message = auditDetails.getMessage();
		String auditId = ("" + auditDetails.getAuditId());
		dynaForm.set("auditId", auditId);
		dynaForm.set("comment", message);

		commonUtility = null;
		auditDetails = null;

		Log.log(Log.INFO, "AdministrationAction", "showReviewAuditPrev",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to get the Audit details for the entered CGPAN.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getAuditForCgpan(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getAuditForCgpan", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		CommonUtility commonUtility = new CommonUtility();
		// Get the CGPAN entered in the screen.
		String CGPAN = (String) dynaForm.get("cgpan");
		Log.log(Log.INFO, "AdministrationAction", "getAuditForCgpan", "CGPAN :"
				+ CGPAN);
		if ((CGPAN == null) || ((CGPAN != null) && (CGPAN.equals("")))) {
			throw new NoDataException("Please enter a valid CGPAN.");
		}
		Administrator admin = new Administrator();
		ArrayList cgpans = admin.getAllCgpans();
		boolean isCGPANInTheSystem = false;
		for (int i = 0; i < cgpans.size(); i++) {
			String pan = (String) cgpans.get(i);
			if (pan == null) {
				continue;
			}
			if (CGPAN.equals(pan)) {
				isCGPANInTheSystem = true;
			}

		}
		if (!isCGPANInTheSystem) {
			throw new NoDataException(
					"The CGPAN does not exist in the Database or may have been expired");
		}

		// Get the audit details for the CGPAN entered and set it to the screen.

		AuditDetails auditDetails = commonUtility.getAuditForCgpan(CGPAN);
		String auditId = ("" + auditDetails.getAuditId());
		dynaForm.set("auditId", auditId);
		dynaForm.set("comment", auditDetails.getMessage());

		commonUtility = null;

		Log.log(Log.INFO, "AdministrationAction", "getAuditForCgpan", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to get the hint question.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getHintQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "getHintQuestion", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String userId = (String) dynaForm.get("userId");

		String memberId = (String) dynaForm.get("memberId");

		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		// If an invalid member id is entered filter it.
		Registration registration = new Registration();
		registration.getMemberDetails(bankId, zoneId, branchId);

		// checks if the user exists for the member

		Administrator administrator = new Administrator();

		User userObj = administrator.getUserInfo(memberId, userId);

		if (userObj == null) {
			throw new NoUserFoundException("No User Found");
		}
		String userMemberId = userObj.getBankId() + userObj.getZoneId()
				+ userObj.getBranchId();

		if (!userMemberId.equals(memberId)) {
			throw new NoUserFoundException("User does not belong to the member");
		}
		if (!userObj.isUserActive()) {
			Log.log(Log.WARNING, "Login", "login", "User is inactive ");

			throw new InactiveUserException("Inactive User");
		}

		Log.log(Log.DEBUG, "AdministrationAction", "getHintQuestion", "userId "
				+ userId);

		// Administrator admin=new Administrator();

		// User user=admin.getUserInfo(userId);

		Hint hint = userObj.getHint();

		String hintQuestion = hint.getHintQuestion();
		String hintAnswer = hint.getHintAnswer();

		if ((hintQuestion == null) && (hintAnswer == null)) {
			throw new NoDataException(
					"No Hint Question/Answer Available for the user.");
		}

		Log.log(Log.DEBUG, "AdministrationAction", "getHintQuestion",
				"hintQuestion " + hintQuestion);

		dynaForm.set("hintQuestion", hintQuestion);
		dynaForm.set("hintAnswer", "");

		// admin=null;
		// user=null;
		hint = null;

		Log.log(Log.INFO, "AdministrationAction", "getHintQuestion", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method is used to check the hint answer .
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown .
	 */
	public ActionForward answerHintQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "answerHintQuestion",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String hintAnswer = (String) dynaForm.get("hintAnswer");

		String userId = (String) dynaForm.get("userId");

		String memberId = (String) dynaForm.get("memberId");

		// String bankId=memberId.substring(0,4);
		// String zoneId=memberId.substring(4,8);
		// String branchId=memberId.substring(8,12);

		Log.log(Log.DEBUG, "AdministrationAction", "answerHintQuestion",
				"userId " + userId);

		Log.log(Log.DEBUG, "AdministrationAction", "answerHintQuestion",
				"hintAnswer " + hintAnswer);

		Administrator admin = new Administrator();

		User user = admin.getUserInfo(memberId, userId);

		Hint hint = user.getHint();

		String forward = "failure";

		if (hintAnswer.equalsIgnoreCase(hint.getHintAnswer())) {
			Log.log(Log.DEBUG, "AdministrationAction", "answerHintQuestion",
					"Hint Answer is same");

			PasswordManager passwordManager = new PasswordManager();

			String decryptedPassword = passwordManager.decryptPassword(user
					.getPassword());
			// System.out.println("Password:"+passwordManager.decryptPassword("752376647728E7D14AF4DC9C3F2E53E9"));
			request.setAttribute("displayPassword", decryptedPassword);

			// Set the display time for the password

			AdminDAO adminDao = new AdminDAO();

			ParameterMaster parameterMaster = adminDao.getParameter();

			int displayPeriod = parameterMaster.getPasswordDisplayPeriod();

			String pwdDisplayPeriod = new Integer(displayPeriod).toString();

			Log.log(Log.DEBUG, "AdministrationAction", "answerHintQuestion",
					"pwdDisplayPeriod " + pwdDisplayPeriod);

			HttpSession session = request.getSession(false);

			session.setAttribute("pwdDisplayPeriod", pwdDisplayPeriod);

			forward = "success";

			passwordManager = null;
		}
		admin = null;
		user = null;
		hint = null;

		Log.log(Log.INFO, "AdministrationAction", "answerHintQuestion",
				"Exited");

		return mapping.findForward(forward);
	}

	// ---------------------------------------------------------------------------------------------------------------//

	// Broadcast Message "After Build 4".
	// changes to be included in def org str from build 3.
	// menu options checked in on 03 Dec.

	/**
	 * This method is used to show the enter broadcast message screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showBroadcastMessage(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showBroadcastMessage",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		// Populate the Bank combo box with all the MLIs.
		Registration registration = new Registration();
		ArrayList mliInfo = registration.getAllMLIs();
		// dynaForm.initialize(mapping);
		int size = mliInfo.size();
		ArrayList banks = new ArrayList();
		for (int i = 0; i < size; i++) {
			MLIInfo mli = (MLIInfo) mliInfo.get(i);
			String shortName = mli.getShortName();
			String bankId = mli.getBankId().trim();
			String zoneId = mli.getZoneId().trim();
			String branchId = mli.getBranchId().trim();
			String memberId = bankId + zoneId + branchId;
			String bank = shortName + "(" + memberId + ")";
			banks.add(bank);
		}
		dynaForm.set("allBanks", banks);

		registration = null;
		mliInfo = null;
		banks = null;
		Log.log(Log.INFO, "AdministrationAction", "showBroadcastMessage",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method populates the combo-boxes according to the radio button
	 * chosen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward actionTaken(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "actionTaken", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Registration registration = new Registration();

		// Get the option selected and bank chosen from dynaForm.
		String radio = (String) dynaForm.get("selectBM");
		String bankName = (String) dynaForm.get("bankName");
		String bankId = null;
		// Retrieve the bank id & zone id.
		if (bankName.equals("Select")) {
			ArrayList blank = new ArrayList();
			dynaForm.set("zones", blank);
			dynaForm.set("branches", blank);
			blank = null;
		} else {
			int start = bankName.indexOf("(");
			int finish = bankName.indexOf(")");
			String memberId = bankName.substring(start + 1, finish);
			dynaForm.set("memberId", memberId);
			bankId = memberId.substring(0, 4);
		}

		// Populate the combo box according to the radio chosen.

		if (radio.equals("membersOfZone") || radio.equals("noOfZones")) {

			ArrayList zoneInfo = new ArrayList();
			if ((bankId != null) && !(bankId.equals(""))) {
				zoneInfo = registration.getZones(bankId);
				ArrayList zones = new ArrayList();
				MLIInfo mliInfo = new MLIInfo();
				int size = zoneInfo.size();
				if (size == 0) {
					zones.add("None");
				} else {
					zones.add("All");
				}

				for (int i = 0; i < size; i++) {
					mliInfo = (MLIInfo) zoneInfo.get(i);
					String zoneName = mliInfo.getZoneName();
					String zoneId = mliInfo.getZoneId();
					String branchId = mliInfo.getBranchId();
					String zone = zoneName + "(" + bankId + zoneId + branchId
							+ ")";
					zones.add(zone);
					mliInfo = null;

				}
				dynaForm.set("zones", zones);
				zoneInfo = null;
			}
		} else if (radio.equals("membersOfBranch")
				|| radio.equals("noOfBranches")) {
			if ((bankId != null) && !(bankId.equals(""))) {

				ArrayList branchInfo = registration.getAllBranches(bankId);
				ArrayList zoneInfo = registration.getZones(bankId);
				ArrayList branches = new ArrayList();
				int branchSize = branchInfo.size();
				int zoneSize = zoneInfo.size();
				if (branchSize == 0 && zoneSize == 0) {
					branches.add("None");
				} else {
					branches.add("All");
				}
				for (int i = 0; i < branchSize; i++) {
					MLIInfo mliInfo = (MLIInfo) branchInfo.get(i);
					String branchName = mliInfo.getBranchName();
					String branchId = mliInfo.getBranchId();
					String zoneId = mliInfo.getZoneId();
					String branch = branchName + "(" + bankId + zoneId
							+ branchId + ")";
					branches.add(branch);
				}
				for (int i = 0; i < zoneSize; i++) {
					MLIInfo mliInfo = (MLIInfo) zoneInfo.get(i);
					String zoneName = mliInfo.getZoneName();
					String zoneId = mliInfo.getZoneId();
					String branchId = mliInfo.getBranchId();
					String zone = zoneName + "(" + bankId + zoneId + branchId
							+ ")";
					branches.add(zone);
					mliInfo = null;

				}
				dynaForm.set("branches", branches);
				branches = null;
				branchInfo = null;

			}
		}

		registration = null;

		Log.log(Log.INFO, "AdministrationAction", "actionTaken", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is to flush the combo boxes of old values.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward flush(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "flush", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String radio = (String) dynaForm.get("selectBM");

		// Create a blank arrayList
		ArrayList blank = new ArrayList();

		// According to the chosen radio button flush the values.
		if (radio.equals("membersOfZone") || radio.equals("noOfZones")
				|| radio.equals("membersOfBranch")
				|| radio.equals("noOfBranches")) {

			dynaForm.set("branches", blank);// flush the branch combo.
			dynaForm.set("zones", blank);// flush the zone combo.
		}

		else if (radio.equals("AllHos") || radio.equals("membersOfBank")
				|| radio.equals("noOfBank") || radio.equals("allMembers")) {

			dynaForm.set("bankName", "");// flush the bank combo.
			dynaForm.set("zones", blank);// flush the zone combo.
			dynaForm.set("branches", blank);// flush the branch combo.
		}

		blank = null;

		Log.log(Log.INFO, "AdministrationAction", "flush", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is used to add the broadcast message details to the database.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addBroadcastMessage(

	ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "addBroadcastMessage",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();
		BroadCastMessage broadCastMessage = new BroadCastMessage();

		String radio = (String) dynaForm.get("selectBM");
		String message = (String) dynaForm.get("broadcastMessage");
		broadCastMessage.setMessage(message);

		// Get the logged in user's userId.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();

		if (radio.equals("AllHos")) {
			broadCastMessage.setAllHos(true);
		} else if (radio.equals("membersOfBank")) {
			broadCastMessage.setMemberOfBank(true);
			String bankName = (String) dynaForm.get("bankName");
			broadCastMessage.setBankName(bankName);
		} else if (radio.equals("membersOfZone")) {

			broadCastMessage.setMembersOfZoOrRO(true);

			String[] zoneNames = (String[]) dynaForm.get("zoneRegionNames");

			/*
			 * If "ALL" is chosen in the combobox message is broadcasted to
			 * members of all the zones of the chosen bank.
			 */

			if (zoneNames[0].equalsIgnoreCase("All")) {

				ArrayList allZones = new ArrayList();

				allZones = (ArrayList) dynaForm.get("zones");

				allZones.remove("All");

				int size = allZones.size();

				String[] zones = new String[size];

				for (int j = 0; j < size; j++) {

					zones[j] = (String) allZones.get(j);

				}

				broadCastMessage.setZoneRegions(zones);
			}

			else {
				broadCastMessage.setZoneRegions(zoneNames);
			}
		} else if (radio.equals("membersOfBranch")) {

			broadCastMessage.setMembersOfBranch(true);

			String[] branchNames = (String[]) dynaForm.get("branchNames");

			/*
			 * If "ALL" is chosen in the combobox message is broadcasted to
			 * members of all the branches of the chosen bank.
			 */

			if (branchNames[0].equalsIgnoreCase("All")) {

				ArrayList allBranches = new ArrayList();

				allBranches = (ArrayList) dynaForm.get("branches");

				allBranches.remove("All");

				int size = allBranches.size();

				String[] branches = new String[size];

				for (int j = 0; j < size; j++) {

					branches[j] = (String) allBranches.get(j);
				}

				broadCastMessage.setBranchNames(branches);

			}

			else {
				broadCastMessage.setBranchNames(branchNames);
			}

		} else if (radio.equals("noOfBank")) {
			broadCastMessage.setNoOfBank(true);
			String bankName = (String) dynaForm.get("bankName");
			broadCastMessage.setBankName(bankName);
		} else if (radio.equals("noOfZones")) {

			broadCastMessage.setNoOfZonesRegions(true);

			String[] zoneNames = (String[]) dynaForm.get("zoneRegionNames");

			/*
			 * If "ALL" is chosen in the combobox message is broadcasted to NO
			 * of all the zones of the chosen bank.
			 */

			if (zoneNames[0].equalsIgnoreCase("All")) {

				ArrayList allZones = new ArrayList();

				allZones = (ArrayList) dynaForm.get("zones");

				allZones.remove("All");

				int size = allZones.size();

				String[] zones = new String[size];

				for (int j = 0; j < size; j++) {

					zones[j] = (String) allZones.get(j);
				}

				broadCastMessage.setZoneRegions(zones);

				allZones.clear();

				allZones = null;

			}

			else {

				broadCastMessage.setZoneRegions(zoneNames);
			}

			zoneNames = null;

		} else if (radio.equals("noOfBranches")) {

			broadCastMessage.setNoOfBranches(true);

			String[] branchNames = (String[]) dynaForm.get("branchNames");

			/*
			 * If "ALL" is chosen in the combobox message is broadcasted to NO
			 * of all the branches of the chosen bank.
			 */

			if (branchNames[0].equalsIgnoreCase("All")) {

				ArrayList allBranches = new ArrayList();

				allBranches = (ArrayList) dynaForm.get("branches");

				allBranches.remove("All");

				int size = allBranches.size();

				String[] branches = new String[size];

				for (int j = 0; j < size; j++) {

					branches[j] = (String) allBranches.get(j);
				}

				broadCastMessage.setBranchNames(branches);

				allBranches.clear();

				allBranches = null;

			} else {

				broadCastMessage.setBranchNames(branchNames);
			}

			branchNames = null;

		} else if (radio.equals("allMembers")) {

			broadCastMessage.setAllMembers(true);
		}

		// Pass the broadcast message object with the final values set to the
		// administrator class.
		administrator.broadcastMessage(broadCastMessage, createdBy);

		administrator = null;
		broadCastMessage = null;
		request.setAttribute("message", "Message to be Broadcasted saved");
		Log.log(Log.INFO, "AdministrationAction", "addBroadcastMessage",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to upload file.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward uploadFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "uploadFile", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		// ArrayList errorMessagesList=new ArrayList();

		if (dynaForm.get("selectMember") != null
				&& !(dynaForm.get("selectMember").equals(""))) {

			mliId = (String) dynaForm.get("selectMember");
			// System.out.println("mliId :" + mliId);
			bankId = mliId.substring(0, 4);
			zoneId = mliId.substring(4, 8);
			branchId = mliId.substring(8, 12);

		} else {

			User user = getUserInformation(request);
			bankId = user.getBankId();
			zoneId = user.getZoneId();
			branchId = user.getBranchId();

		}
		String forward = "";

		FormFile formFile = (FormFile) dynaForm.get("uploadFile");

		if (formFile != null) {
			String contextPath = request.getSession(false).getServletContext()
					.getRealPath("");
			FileUploading fileUploading = new FileUploading(formFile,
					contextPath, request, bankId, zoneId, branchId);

			// HttpSession session=request.getSession(false);
			fileUploading.process();

			ArrayList msgList = fileUploading.messagesList;

			if (msgList != null && msgList.size() != 0) {
				dynaForm.set("errorMessagesList", msgList);
				forward = "errorMessagesPage";

			} else {
				request.setAttribute("message", "Details Uploaded Successfully");

				forward = "success";
			}

			// session.setAttribute("fileUploading",fileUploading);

			// Thread thread=new Thread(fileUploading);
			// thread.start();
			// uploadFile(formFile,contextPath);

		}

		Log.log(Log.INFO, "AdministrationAction", "uploadFile", "Exited");

		return mapping.findForward(forward);
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward uploadFileNew(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "uploadFileNew", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		// ArrayList errorMessagesList=new ArrayList();

		if (dynaForm.get("selectMember") != null
				&& !(dynaForm.get("selectMember").equals(""))) {

			mliId = (String) dynaForm.get("selectMember");
			// System.out.println("mliId :" + mliId);
			bankId = mliId.substring(0, 4);
			zoneId = mliId.substring(4, 8);
			branchId = mliId.substring(8, 12);

		} else {

			User user = getUserInformation(request);
			bankId = user.getBankId();
			zoneId = user.getZoneId();
			branchId = user.getBranchId();

		}
		String forward = "";
		System.out.println("MemberId:" + bankId + zoneId + branchId);
		FormFile formFile = (FormFile) dynaForm.get("uploadFile");
		// File formFile = new File(file.getFileName());

		if (formFile != null) {
			CommonDAO commonDAO = new CommonDAO();
			String contextPath = request.getSession(false).getServletContext()
					.getRealPath("/")
					+ "Uploaded Files";
			// FileUploading fileUploading=new
			// FileUploading(formFile,contextPath,request,bankId,zoneId,branchId);
			BufferedInputStream bis = null;
			DataInputStream dis = null;
			try {
				System.out.println("File Name:" + formFile.getFileName());

				// System.out.println("contextPath:"+contextPath);
				// File file = new
				// File("C:/Documents and Settings/path.CGTSI/My Documents/TEST_INTERFACE.txt");
				// fis = new FileInputStream(file);
				/*
				 * bis = new BufferedInputStream(formFile.getInputStream()); dis
				 * = new DataInputStream(bis); while(dis.available()!=0){ String
				 * testData = (String)dis.readLine();
				 * 
				 * String[] temp; String delimeter = "\\|"; temp =
				 * testData.split(delimeter); for(int i =0; i < temp.length ;
				 * i++) System.out.println(temp[i]); //
				 * System.out.println(testData); }
				 */
				if (!formFile.getFileName().equals("")) {
					// System.out.println("Server path:" +contextPath);
					// Create file
					File fileToCreate = new File(contextPath,
							formFile.getFileName());
					// If file does not exists create file
					if (!fileToCreate.exists()) {
						FileOutputStream fileOutStream = new FileOutputStream(
								fileToCreate);
						fileOutStream.write(formFile.getFileData());
						// commonDAO.uploadFileApplications(contextPath,formFile.getFileName());
						fileOutStream.flush();
						fileOutStream.close();
					}
				}

				// bis.close();
				// dis.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// fileUploading.process();

			// ArrayList msgList = fileUploading.messagesList;

			// if(msgList!=null && msgList.size()!=0)
			// {
			// dynaForm.set("errorMessagesList",msgList);
			// forward="errorMessagesPage";

			// }else{
			// request.setAttribute("message","Details Uploaded Successfully");

			forward = "success";
			// }

			// session.setAttribute("fileUploading",fileUploading);

			// Thread thread=new Thread(fileUploading);
			// thread.start();
			// uploadFile(formFile,contextPath);

		}

		Log.log(Log.INFO, "AdministrationAction", "uploadFileNew", "Exited");

		return mapping.findForward(forward);
	}

	/**
	 * This method is used get the users of a member who does not have active
	 * roles and privileges.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getUsersWithNoRolesForMember(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getUsersForMember",
				"Entered");

		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("memberId");

		// The active userIds for the entered memberId is populated from the
		// database.
		ArrayList userIds = admin.getUsersWithoutRolesAndPrivileges(memberId);
		HttpSession session = (HttpSession) request.getSession(false);
		session.setAttribute("MemberSelected", "Y");
		dynaForm.set("activeUsers", userIds);

		userIds = null;

		Log.log(Log.INFO, "AdministrationAction", "getUsersForMember", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method is used to display a list of members upon selecting the
	 * members the list of users for whom roles to be assigned or modified.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */

	public ActionForward selectUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "selectUser", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		dynaActionForm.initialize(mapping);

		Registration registration = new Registration();

		ArrayList members = registration.getAllMembers();

		ArrayList memberIds = new ArrayList(members.size());

		for (int i = 0; i < members.size(); i++) {
			MLIInfo mliInfo = (MLIInfo) members.get(i);

			memberIds.add(mliInfo.getBankId() + mliInfo.getZoneId()
					+ mliInfo.getBranchId());
		}

		memberIds.add(Constants.CGTSI_USER_BANK_ID
				+ Constants.CGTSI_USER_BANK_ID + Constants.CGTSI_USER_BANK_ID);

		// Populate the combo box with all the member ids.
		dynaActionForm.set("memberIds", memberIds);

		registration = null;
		members = null;
		memberIds = null;

		Log.log(Log.INFO, "AdministrationAction", "selectUser", "Exited");

		return mapping.findForward("success");
	}

	class FileUploading// implements Runnable
	{
		private FormFile formFile = null;
		private String contextPath = null;
		private HttpServletRequest request = null;
		private String bankId = "";
		private String zoneId = "";
		private String branchId = "";
		private ArrayList messagesList = new ArrayList();

		public void setRequest(HttpServletRequest request) {
			Log.log(Log.DEBUG, "AdministrationAction", "run",
					"Setting the request ");
			this.request = request;
		}

		FileUploading(FormFile formFile, String path,
				HttpServletRequest request, String memberBankId,
				String memberZoneId, String memberBranchId) {
			this.formFile = formFile;
			contextPath = path;
			this.request = request;
			this.bankId = memberBankId;
			this.zoneId = memberZoneId;
			this.branchId = memberBranchId;
			Log.log(Log.DEBUG, "AdministrationAction", "run",
					"constructor request ... " + request);
		}

		public void process() {
			Log.log(Log.DEBUG, "AdministrationAction", "run", "request ... "
					+ request);

			HttpSession session = request.getSession(false);

			try {
				session.setAttribute(SessionConstants.FILE_UPLOAD_STATUS,
						new Integer(Constants.FILE_UPLOAD_DEFAULT_STATUS));

				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"before uploading");
				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"context path  " + contextPath);

				File uploadedFile = uploadFile(formFile, contextPath);

				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"After uploading");

				String subMenuItem = (String) session
						.getAttribute("subMenuItem");

				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"subMenuItem " + subMenuItem);

				Log.log(Log.DEBUG, "AdministrationAction", "run", "filename "
						+ uploadedFile.getName());

				if (uploadedFile.getName().endsWith(".ARC")) {
					FileInputStream fileInput = new FileInputStream(
							uploadedFile);
					ObjectInputStream objInput = new ObjectInputStream(
							fileInput);
					Hashtable fileDetails = (Hashtable) objInput.readObject();

					objInput.close();
					objInput = null;
					fileInput.close();
					fileInput = null;

					/*
					 * File versionFile = new File(contextPath +
					 * "\\Download\\Version.txt");
					 * Log.log(Log.DEBUG,"AdministrationAction"
					 * ,"run","filename "+ versionFile.getName()); FileReader
					 * fileReader = new FileReader(versionFile); BufferedReader
					 * buffReader = new BufferedReader(fileReader); String line
					 * = buffReader.readLine().trim(); String version =
					 * line.substring(line.indexOf("version - "),
					 * line.length()); //latest version
					 * Log.log(Log.DEBUG,"AdministrationAction"
					 * ,"run","version "+ version);
					 * System.out.println("version " + version);
					 */

					File file = new File(contextPath
							+ "\\Download\\Version.properties");
					Properties versionProp = new Properties();
					File versionFile = new File(file.getAbsolutePath());
					FileInputStream fin = new FileInputStream(versionFile);
					versionProp.load(fin);

					String version = versionProp.getProperty("version");

					// System.out.println("version " + version);

					String uploadVersion = (String) fileDetails.get("version"); // version
																				// from
																				// the
																				// upload
																				// file
					Log.log(Log.DEBUG, "AdministrationAction", "run",
							"upload version " + uploadVersion);

					if (!version.equalsIgnoreCase(uploadVersion)) {
						messagesList
								.add("ThinClient version has been changed. Please dowonload the latest version.");
					}
				} else {
					messagesList.add("Not a valid File for Upload.");
				}

				FileUploader fileUploader = new FileUploader();
				if (messagesList.size() == 0) {
					User user = getUserInformation(request);
					String userId = user.getUserId();

					// ArrayList appErrorMessages=new ArrayList();
					// ArrayList claimErrorMessages=new ArrayList();
					// ArrayList gmErrorMessages=new ArrayList();
					ArrayList errorMessages = new ArrayList();

					if (subMenuItem.equals(MenuOptions_back
							.getMenu(MenuOptions_back.THIN_CLIENT_AP_FILE_UPLOAD))) {

						// Process the applications.
						Log.log(Log.DEBUG, "AdministrationAction", "run",
								"Process applications");

						errorMessages = fileUploader.uploadFiles(uploadedFile,
								AdminConstants.APPLICATION_FILES, userId,
								bankId, zoneId, branchId);
						this.messagesList = errorMessages;
						// errorMessages.add(appErrorMessages);

					} else if (subMenuItem.equals(MenuOptions_back
							.getMenu(MenuOptions_back.THIN_CLIENT_CP_FILE_UPLOAD))) {
						// Process the claim applications

						Log.log(Log.DEBUG, "AdministrationAction", "run",
								"Process claim applications");

						errorMessages = fileUploader.uploadFiles(uploadedFile,
								AdminConstants.CLAIM_FILES, userId, bankId,
								zoneId, branchId);
						this.messagesList = errorMessages;
						// System.out.println(messagesList.size());
						// errorMessages.add(claimErrorMessages);

					} else if (subMenuItem.equals(MenuOptions_back
							.getMenu(MenuOptions_back.THIN_CLIENT_GM_FILE_UPLOAD))) {
						// Process the periodic info files.

						Log.log(Log.DEBUG, "AdministrationAction", "run",
								"Process periodic info");

						errorMessages = fileUploader.uploadFiles(uploadedFile,
								AdminConstants.PERIODIC_FILES, userId, bankId,
								zoneId, branchId);
						this.messagesList = errorMessages;
						// errorMessages.add(gmErrorMessages);
					} else {
						// Invalid entry. ignore.

						Log.log(Log.WARNING, "AdministrationAction", "run",
								"Invalid entry. ignore");
					}
				}

				fileUploader = null;

				session.setAttribute(SessionConstants.FILE_UPLOAD_STATUS,
						new Integer(Constants.FILE_UPLOAD_SUCCESS));

				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"After setting the upload status");
			} catch (Exception e) {
				Log.log(Log.ERROR, "AdministrationAction", "run",
						e.getMessage());
				Log.logException(e);
				session.setAttribute(SessionConstants.FILE_UPLOAD_STATUS,
						new Integer(Constants.FILE_UPLOAD_FAILED));
				Log.log(Log.DEBUG, "AdministrationAction", "run",
						"Constants.FILE_UPLOAD_FAILED "
								+ Constants.FILE_UPLOAD_FAILED);
			}
		}
	}

	/**
	 * This method is used to perform the day end processes.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward dayEndProcess(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "dayEndProcess", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		String[] dayEndProcesses = (String[]) dynaForm.get("dayEndProcesses");

		int size = dayEndProcesses.length;

		Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
				"size of day end process array  " + size);

		User user = getUserInformation(request);
		if (size != 0) {
			for (int i = 0; i < size; i++) {
				String optionChosen = dayEndProcesses[i];
				Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
						"optionChosen is " + optionChosen);
				RpProcessor rpProcessor = new RpProcessor();
				ScheduledProcessManager scheduledProcessManager = new ScheduledProcessManager();

				if (optionChosen.equals("removeData")) {
					scheduledProcessManager.procRemDemoUser();
					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"Data entered by demo user removed from internet database ");
				}

				if (optionChosen.equals("loanClosing")) {
					scheduledProcessManager.updateAppExpiry();
					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"Loans at tenure end date closed ");
				}

				if (optionChosen.equals("moveData")) {
					scheduledProcessManager.adminMailInfo();
					scheduledProcessManager.tcOutstanding();
					scheduledProcessManager.wcOutstanding();
					scheduledProcessManager.dbrDetail();
					scheduledProcessManager.donorDetail();
					scheduledProcessManager.memberInfo();
					scheduledProcessManager.npaDetail();
					scheduledProcessManager.legalDetail();
					scheduledProcessManager.plr();
					scheduledProcessManager.recoveryDetail(user);
					scheduledProcessManager.recoveryActionDetail();
					scheduledProcessManager.repaymentDetail();
					// scheduledProcessManager.repaymentSchemeDetail();
					scheduledProcessManager.userInfo();
					scheduledProcessManager.userRoles();
					scheduledProcessManager.userPrivileges();
					scheduledProcessManager.userActiveDeactiveLog();

					Log.log(Log.DEBUG,
							"AdministrationAction",
							"dayEndProcess",
							"Data not requiring CGTSI approval moved from temporary database to Intranet database ");
				}

				if (optionChosen.equals("generateCGDan")) {

					rpProcessor.generateCGDAN(user, Constants.ALL);

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"generateCGDAN executed");
				}

				if (optionChosen.equals("generateCLDan")) {

					rpProcessor.generateCLDAN(user, null, null, null);

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"generateCLDAN executed");
				}

				if (optionChosen.equals("generateSFDan")) {

					rpProcessor.generateSFDAN(user, null, null, null);

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"generateSFDan executed");
				}

				if (optionChosen.equals("generateShortDan")) {

					rpProcessor.generateSHDAN(user, Constants.ALL);

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"generateShortDan executed");
				}
				if (optionChosen.equals("calculatePenalty")) {

					rpProcessor.calculatePenaltyForOverdueDANs(user);

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"generateShortDan executed");
				}
				if (optionChosen.equals("batchProcess")) {

					Administrator admin = new Administrator();

					admin.batchProcess();

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"batchProcess executed");
				}

				if (optionChosen.equals("archiveData")) {

					Administrator admin = new Administrator();

					admin.archiveData();

					Log.log(Log.DEBUG, "AdministrationAction", "dayEndProcess",
							"batchProcess executed");
				}

			}
		}

		dayEndProcesses = null;
		user = null;
		request.setAttribute("message", "Day end processes performed");
		Log.log(Log.INFO, "AdministrationAction", "dayEndProcess", "Exited");

		return mapping.findForward("success");
	}

	/**
	 * This method checks if the logged in user is a CGTSI User or MLI User and
	 * sets the MLI ID
	 * 
	 * @author SS14485
	 */
	public ActionForward getMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		// HttpSession session=request.getSession(false);

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String forward;

		// If CGTSI User
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			forward = "uploadMliPage";

		} else {

			// MLI User

			MLIInfo mliInfo = getMemberInfo(request);
			bankId = mliInfo.getBankId();
			String branchId = mliInfo.getBranchId();
			String zoneId = mliInfo.getZoneId();
			// String mliId=bankId + zoneId + branchId;

			forward = "uploadPage";

		}

		return mapping.findForward(forward);

	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getMliInfoNew(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		System.out.println("getMliInfoNew Entered:");
		// HttpSession session=request.getSession(false);

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String forward;

		// If CGTSI User
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			forward = "uploadMliPage";

		} else {

			// MLI User

			MLIInfo mliInfo = getMemberInfo(request);
			bankId = mliInfo.getBankId();
			String branchId = mliInfo.getBranchId();
			String zoneId = mliInfo.getZoneId();
			// String mliId=bankId + zoneId + branchId;

			forward = "uploadPage";

		}

		return mapping.findForward(forward);

	}

	/**
	 * Retrieves the member ID entered by the CGTSI User
	 */
	public ActionForward showFileUploadApp(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String forward="";

		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("selectMember");

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
				"MEmber Id :" + memberId);

		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		Registration registration = new Registration();

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
		}

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}
		return mapping.findForward("uploadPage");

	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward showFileUploadAppNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String forward="";
		System.out.println("showFileUploadAppNew Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		String memberId = (String) dynaForm.get("selectMember");

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
				"MEmber Id :" + memberId);

		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		Registration registration = new Registration();

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
		}

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}
		return mapping.findForward("uploadPage");

	}

	/**
	 * This method is for downloading the thin client file.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward downloadThinClient(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "downloadThinClient",
				"Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();

		// If CGTSI user logs in he can download both CGFSI and MCGF files.
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			Log.log(Log.INFO, "AdministrationAction", "downloadThinClient",
					"Exited");
			return mapping.findForward("CGTSIUser");

		}
		// If MLI user logs in his MCGF status is checked and forwarded
		// appropriately.
		else {
			Registration registration = new Registration();
			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);
			String mcgfStatus = mliInfo.getSupportMCGF();
			dynaActionForm.set("MCGFStatus", mcgfStatus);
			Log.log(Log.DEBUG, "AdministrationAction", "downloadThinClient",
					"mcgfStatus" + mcgfStatus);
			Log.log(Log.INFO, "AdministrationAction", "downloadThinClient",
					"Exited");
			return mapping.findForward("MLIUser");
		}
	}

	/**
	 * This method is for modifying the member details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getMemberDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getMemberDetails", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		Registration registration = new Registration();

		String memberId = (String) dynaActionForm.get("memberId");
		// System.out.println("MemberId:"+memberId);
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		// STATES combobox is populated from the state master table.
		Administrator admin = new Administrator();
		ArrayList states = admin.getAllStates();
		dynaActionForm.set("states", states);

		// District combobox is populated from the state master table.
		String state = mliInfo.getState();
		// System.out.println("State:"+state);
		// Administrator administrator=new Administrator();
		ArrayList districts = admin.getAllDistricts(state);
		dynaActionForm.set("districts", districts);

		BeanUtils.copyProperties(dynaActionForm, mliInfo);

		String reportingZoneId = mliInfo.getReportingZoneID();

		Log.log(Log.DEBUG, "AdministrationAction", "getMemberDetails",
				"reportingZoneId " + reportingZoneId);

		if (reportingZoneId == null || reportingZoneId.equals("")) {

			dynaActionForm.set("reportingZone", "NIL");
		}

		else {

			dynaActionForm.set("reportingZone", reportingZoneId);

		}

		String address = mliInfo.getAddress();
		dynaActionForm.set("address", address);

		Log.log(Log.DEBUG, "AdministrationAction", "getMemberDetails",
				"address " + address);

		String mcgfOption = mliInfo.getSupportMCGF();

		Log.log(Log.DEBUG, "AdministrationAction", "getMemberDetails",
				"mcgfOption " + mcgfOption);

		dynaActionForm.set("supportMCGF", mcgfOption);

		Log.log(Log.DEBUG, "AdministrationAction", "getMemberDetails",
				"memberId " + memberId);

		Log.log(Log.INFO, "AdministrationAction", "getMemberDetails", "Exited");
		return mapping.findForward("success");

	}

	/**
	 * This method is for updating the modified member details.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward updateMemberDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "getMemberDetails", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		// System.out.println("EmailId:"+request.getParameter("emailId"));
		String emailId = (String) dynaActionForm.get("emailId");
		// System.out.println("emailId:"+emailId);
		MLIInfo mliDetails = new MLIInfo();

		BeanUtils.populate(mliDetails, dynaActionForm.getMap());

		String memberId = (String) dynaActionForm.get("memberId");
		// System.out.println();
		String bankId = memberId.substring(0, 4);
		// System.out.println("Bank Id:"+bankId);

		String zoneId = memberId.substring(4, 8);

		String branchId = memberId.substring(8, 12);

		mliDetails.setBankId(bankId);

		mliDetails.setZoneId(zoneId);

		mliDetails.setBranchId(branchId);

		mliDetails.setEmailId(emailId);

		Registration registration = new Registration();

		Log.log(Log.DEBUG, "AdministrationAction", "updateMemberDetails",
				"memberId " + memberId);

		User user = getUserInformation(request);

		String memberBankId = user.getBankId();
		// System.out.println("Member Bank Id:"+memberBankId);
		String createdby = user.getUserId();

		registration.updateMemberDetails(createdby, mliDetails);

		Log.log(Log.INFO, "AdministrationAction", "getMemberDetails", "Exited");

		request.setAttribute("message", "Member Details Updated");

		return mapping.findForward("success");

	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward updateMemberAddressDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "updateMemberAddressDetails",
				"Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		// System.out.println("EmailId:"+request.getParameter("emailId"));
		String emailId = (String) dynaActionForm.get("emailId");
		// System.out.println("emailId:"+emailId);
		MLIInfo mliDetails = new MLIInfo();

		BeanUtils.populate(mliDetails, dynaActionForm.getMap());

		String memberId = (String) dynaActionForm.get("memberId");
		// System.out.println();
		String bankId = memberId.substring(0, 4);
		// System.out.println("Bank Id:"+bankId);

		String zoneId = memberId.substring(4, 8);

		String branchId = memberId.substring(8, 12);

		mliDetails.setBankId(bankId);

		mliDetails.setZoneId(zoneId);

		mliDetails.setBranchId(branchId);

		mliDetails.setEmailId(emailId);

		Registration registration = new Registration();

		Log.log(Log.DEBUG, "AdministrationAction",
				"updateMemberAddressDetails", "memberId " + memberId);

		User user = getUserInformation(request);

		String memberBankId = user.getBankId();
		// System.out.println("Member Bank Id:"+memberBankId);
		String createdby = user.getUserId();
		System.out.println("Member Id:"
				+ bankId.concat(zoneId).concat(branchId));
		System.out.println("Email Id:" + emailId);
		System.out.println("User Id:" + createdby);

		registration.updateMemberAddressDetails(createdby, mliDetails, emailId);

		Log.log(Log.INFO, "AdministrationAction", "updateMemberAddressDetails",
				"Exited");

		request.setAttribute("message", "Member Details Updated");

		return mapping.findForward("success");

	}

	/**
	 * This method is used to show the modify MLI screen.
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showModifyMLI(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyMLI", "Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);

		// Get the logged-in users information.
		User loggedUser = getUserInformation(request);
		String bankId = loggedUser.getBankId();
		String zoneId = loggedUser.getZoneId();
		String branchId = loggedUser.getBranchId();
		if ((bankId.equals(Constants.CGTSI_USER_BANK_ID))
				&& (bankId.equals(Constants.CGTSI_USER_ZONE_ID))
				&& (bankId.equals(Constants.CGTSI_USER_BRANCH_ID))) {
			return mapping.findForward("CGTSIUser");
		} else {

			String memberId = bankId + zoneId + branchId;
			dynaActionForm.set("memberId", memberId);

			Registration registration = new Registration();

			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);

			// STATES combobox is populated from the state master table.
			Administrator admin = new Administrator();
			ArrayList states = admin.getAllStates();
			dynaActionForm.set("states", states);

			// District combobox is populated from the state master table.
			String state = mliInfo.getState();
			// Administrator administrator=new Administrator();
			ArrayList districts = admin.getAllDistricts(state);
			dynaActionForm.set("districts", districts);

			BeanUtils.copyProperties(dynaActionForm, mliInfo);

			String reportingZoneId = mliInfo.getReportingZoneID();

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"reportingZoneId " + reportingZoneId);

			if (reportingZoneId == null || reportingZoneId.equals("")) {

				dynaActionForm.set("reportingZone", "NIL");
			}

			else {

				dynaActionForm.set("reportingZone", reportingZoneId);

			}

			String address = mliInfo.getAddress();
			dynaActionForm.set("address", address);

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"address " + address);

			String mcgfOption = mliInfo.getSupportMCGF();

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"mcgfOption " + mcgfOption);

			dynaActionForm.set("supportMCGF", mcgfOption);

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"memberId " + memberId);

			Log.log(Log.INFO, "AdministrationAction", "showModifyMLI", "Exited");

			return mapping.findForward("success");
		}
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */

	public ActionForward showModifyMLIAddress(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showModifyMLIAddress",
				"Entered");
		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.initialize(mapping);
		HttpSession session = request.getSession(false);
		// Get the logged-in users information.
		User loggedUser = getUserInformation(request);
		String bankId = loggedUser.getBankId();
		String zoneId = loggedUser.getZoneId();
		String branchId = loggedUser.getBranchId();
		if ((bankId.equals(Constants.CGTSI_USER_BANK_ID))
				&& (bankId.equals(Constants.CGTSI_USER_ZONE_ID))
				&& (bankId.equals(Constants.CGTSI_USER_BRANCH_ID))) {
			dynaActionForm.set("memberId", "");
			session.setAttribute(SessionConstants.TARGET_URL,
					"selectMemberForUpdate.do?method=selectMemberForUpdate");
			return mapping.findForward("CGTSIUser");
		} else {

			String memberId = bankId + zoneId + branchId;
			dynaActionForm.set("memberId", memberId);

			Registration registration = new Registration();

			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);

			// STATES combobox is populated from the state master table.
			Administrator admin = new Administrator();
			ArrayList states = admin.getAllStates();
			dynaActionForm.set("states", states);

			// District combobox is populated from the state master table.
			String state = mliInfo.getState();
			// Administrator administrator=new Administrator();
			ArrayList districts = admin.getAllDistricts(state);
			dynaActionForm.set("districts", districts);

			BeanUtils.copyProperties(dynaActionForm, mliInfo);

			String reportingZoneId = mliInfo.getReportingZoneID();

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLIAddress",
					"reportingZoneId " + reportingZoneId);

			if (reportingZoneId == null || reportingZoneId.equals("")) {

				dynaActionForm.set("reportingZone", "NIL");
			}

			else {

				dynaActionForm.set("reportingZone", reportingZoneId);

			}
			String zoneName = mliInfo.getZoneName();
			System.out.println("zoneName:" + zoneName);
			if (zoneName == null || zoneName.equals("")) {
				dynaActionForm.set("zoneName", "NIL");
			} else {
				dynaActionForm.set("zoneName", zoneName);
			}
			String branchName = mliInfo.getBranchName();
			System.out.println("branch Name:" + branchName);
			if (branchName == null || branchName.equals("")) {
				dynaActionForm.set("branchName", "NIL");
			} else {
				dynaActionForm.set("branchName", branchName);
			}

			String address = mliInfo.getAddress();
			dynaActionForm.set("address", address);

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"address " + address);

			String mcgfOption = mliInfo.getSupportMCGF();

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"mcgfOption " + mcgfOption);

			dynaActionForm.set("supportMCGF", mcgfOption);

			Log.log(Log.DEBUG, "AdministrationAction", "showModifyMLI",
					"memberId " + memberId);

			Log.log(Log.INFO, "AdministrationAction", "showModifyMLI", "Exited");

			return mapping.findForward("success");
		}
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward selectMemberForUpdate(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "selectMemberForUpdate",
				"Entered");
		Registration registration = new Registration();
		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// Retrieve the memberId entered in the screen.
		String memberId = (String) dynaActionForm.get("memberId");
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		dynaActionForm.set("memberId", memberId);

		// Registration registration=new Registration();

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		// STATES combobox is populated from the state master table.
		Administrator admin = new Administrator();
		ArrayList states = admin.getAllStates();
		dynaActionForm.set("states", states);

		// District combobox is populated from the state master table.
		String state = mliInfo.getState();
		// Administrator administrator=new Administrator();
		ArrayList districts = admin.getAllDistricts(state);
		dynaActionForm.set("districts", districts);

		BeanUtils.copyProperties(dynaActionForm, mliInfo);

		String reportingZoneId = mliInfo.getReportingZoneID();

		Log.log(Log.DEBUG, "AdministrationAction", "selectMemberForUpdate",
				"reportingZoneId " + reportingZoneId);

		if (reportingZoneId == null || reportingZoneId.equals("")) {

			dynaActionForm.set("reportingZone", "NIL");
		}

		else {

			dynaActionForm.set("reportingZone", reportingZoneId);

		}
		String zoneName = mliInfo.getZoneName();
		System.out.println("zoneName:" + zoneName);
		if (zoneName == null || zoneName.equals("")) {
			dynaActionForm.set("zoneName", "NIL");
		} else {
			dynaActionForm.set("zoneName", zoneName);
		}
		String branchName = mliInfo.getBranchName();
		System.out.println("branch Name:" + branchName);
		if (branchName == null || branchName.equals("")) {
			dynaActionForm.set("branchName", "NIL");
		} else {
			dynaActionForm.set("branchName", branchName);
		}

		String address = mliInfo.getAddress();
		dynaActionForm.set("address", address);

		Log.log(Log.DEBUG, "AdministrationAction", "selectMemberForUpdate",
				"address " + address);

		String mcgfOption = mliInfo.getSupportMCGF();

		Log.log(Log.DEBUG, "AdministrationAction", "selectMemberForUpdate",
				"mcgfOption " + mcgfOption);

		dynaActionForm.set("supportMCGF", mcgfOption);

		Log.log(Log.INFO, "AdministrationAction", "selectMemberForUpdate",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward showUpdateState(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showUpdateState", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();

		HttpSession session = request.getSession(false);
		session.setAttribute("modFlag", "0");

		ArrayList states = administrator.getAllStateCodes();
		dynaForm.set("states", states);

		administrator = null;

		Log.log(Log.INFO, "AdministrationAction", "showUpdateState", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showStateName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showStateName", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Administrator administrator = new Administrator();

		HttpSession session = request.getSession(false);
		session.setAttribute("modFlag", "1");

		String stateCode = (String) dynaForm.get("stateCode");

		String state = "";
		if (!stateCode.equals("")) {
			state = administrator.getStateName(stateCode);
		}
		dynaForm.set("stateName", state);

		administrator = null;

		Log.log(Log.INFO, "AdministrationAction", "showStateName", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward updateState(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "updateState", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator administrator = new Administrator();

		StateMaster stateMaster = new StateMaster();

		BeanUtils.populate(stateMaster, dynaForm.getMap());

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		stateMaster.setCreatedBy(createdBy);

		administrator.modifyStateMaster(stateMaster);

		administrator = null;

		request.setAttribute("message", "State Updated successfully");

		Log.log(Log.INFO, "AdministrationAction", "updateState", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showUpdateDistrict(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showUpdateDistrict",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Administrator administrator = new Administrator();

		HttpSession session = request.getSession(false);
		session.setAttribute("modFlag", "0");

		// Populate the states combo box.
		ArrayList states = administrator.getAllStates();
		dynaForm.set("states", states);

		administrator = null;
		states = null;

		Log.log(Log.INFO, "AdministrationAction", "showUpdateDistrict",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward showDistricts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDistricts", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator administrator = new Administrator();

		HttpSession session = request.getSession(false);
		session.setAttribute("modFlag", "1");

		String stateName = (String) dynaForm.get("stateName");
		ArrayList districtList = administrator.getAllDistricts(stateName);
		dynaForm.set("districts", districtList);

		administrator = null;
		districtList = null;

		Log.log(Log.INFO, "AdministrationAction", "showDistricts", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showDistrictName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "showDistrictName", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession session = request.getSession(false);
		session.setAttribute("modFlag", "2");

		String distName = (String) dynaForm.get("districtName");

		dynaForm.set("modDistrictName", distName);

		Log.log(Log.INFO, "AdministrationAction", "showDistrictName", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward updateDistrict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "AdministrationAction", "updateDistrict", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		DistrictMaster districtMaster = new DistrictMaster();
		BeanUtils.populate(districtMaster, dynaForm.getMap());
		Administrator administrator = new Administrator();

		// Get the logged-in users id and set it in the master class.
		User user = getUserInformation(request);
		String createdBy = user.getUserId();
		districtMaster.setCreatedBy(createdBy);
		String modDistrict = (String) dynaForm.get("modDistrictName");

		administrator.modifyDistrictmaster(districtMaster, modDistrict);

		districtMaster = null;
		user = null;
		request.setAttribute("message", "District Updated Successfully");
		Log.log(Log.INFO, "AdministrationAction", "updateDistrict", "Exited");
		return mapping.findForward("success");
	}

	/* Bpraj begin */
	public ActionForward saveAccDetail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "saveAccDetail", "Entered");

		Administrator addAccount = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String mliName = (String) dynaForm.get("member");
		String memberId = (String) dynaForm.get("memberId");
		String phoneNo = (String) dynaForm.get("phoneNo");
		System.out.println("...................................." + phoneNo);
		String phone = (String) dynaForm.get("phone");
		System.out.println(".................................." + phone);
		String emailId = (String) dynaForm.get("emailId");
		String beneficiary = (String) dynaForm.get("beneficiary");
		String accountType = (String) dynaForm.get("accountType");
		System.out.println("saveAccDetail..."+accountType);
		String branchId = (String) dynaForm.get("branchId");
		String micrCode = (String) dynaForm.get("micrCode");
		System.out.println("saveAccDetail..."+micrCode);
		String accNo = (String) dynaForm.get("accNo");
		String rtgsNO = (String) dynaForm.get("rtgsNO");
		String neftNO = (String) dynaForm.get("neftNO");

		int no = addAccount.addAccountDetails(mliName, memberId, phoneNo,
				phone, emailId, beneficiary, accountType, branchId, micrCode,
				accNo, rtgsNO, neftNO);

		if (no > 0) {

			request.setAttribute("message", "Account Details Added");
		} else {
			request.setAttribute("message", "Account Details Not Added");

		}

		return mapping.findForward("success");
	}

	public ActionForward updateAccount(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "updateAccount", "Entered");
		Administrator addAccount = new Administrator();
		String mliId = "";
		ArrayList memberIdList = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String memberId = (String) dynaForm.get("memberId");
		ArrayList accInfo = addAccount.getAccInfo(memberId);

		if (accInfo.size() > 0) {
			dynaForm.set("member", accInfo.get(0));
			dynaForm.set("memberId", accInfo.get(1));
			dynaForm.set("phoneNo", accInfo.get(2));
			dynaForm.set("phone", accInfo.get(3));
			dynaForm.set("emailId", accInfo.get(4));
			dynaForm.set("beneficiary", accInfo.get(5));
			dynaForm.set("accountType", accInfo.get(6));
			dynaForm.set("branchId", accInfo.get(7));
			dynaForm.set("micrCode", accInfo.get(8));
			dynaForm.set("accNo", accInfo.get(9));
			dynaForm.set("rtgsNO", accInfo.get(10));
			dynaForm.set("neftNO", accInfo.get(11));

		}

		return mapping.findForward("updateDetail");
	}

	public ActionForward updateAccDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "updateAccDetail", "Enter");
		Administrator addAccount = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);

		String mliName = (String) dynaForm.get("member");
		String memberId = (String) dynaForm.get("memberId");
		String phoneNo = (String) dynaForm.get("phoneNo");

		String phone = (String) dynaForm.get("phone");
		String emailId = (String) dynaForm.get("emailId");
		String beneficiary = (String) dynaForm.get("beneficiary");
		String accountType = (String) dynaForm.get("accountType");
		System.out.println("AA...updateAccDetail..accountType"+accountType);
		String branchId = (String) dynaForm.get("branchId");
		String micrCode = (String) dynaForm.get("micrCode");
		System.out.println("AA...updateAccDetail..micrCode"+micrCode);
		String accNo = (String) dynaForm.get("accNo");
		String rtgsNO = (String) dynaForm.get("rtgsNO");
		String neftNO = (String) dynaForm.get("neftNO");

		// int
		// no=addAccount.updateAccountDetails(mliName,memberId,phoneNo,phone,emailId,

		int no = addAccount.updateAccountDetails(mliName, memberId, phoneNo,
				phone, emailId,

				beneficiary, accountType, branchId, micrCode, accNo, rtgsNO,
				neftNO);

		if (no > 0) {

			request.setAttribute("message", "Account Details updated");
		} else {
			request.setAttribute("message", "Account Details Not updated");

		}
		return mapping.findForward("success");
	}

	/* Bpraj */

	/* Bpraj begin Mail */
	public ActionForward Mail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Message message = new Message();

		BeanUtils.populate(message, dynaForm.getMap());

		User user = getUserInformation(request);

		message.setFrom(user.getUserId());

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail",
				"From " + user.getUserId());

		ArrayList to = new ArrayList();

		to.add(dynaForm.get("userId"));

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail",
				"To " + dynaForm.get("userId"));

		message.setTo(to);

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "from "
				+ message.getFrom());

		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "subject "
				+ message.getSubject());
		Log.log(Log.DEBUG, "AdministrationAction", "sendMail", "message "
				+ message.getMessage());

		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Entered");

		Administrator admin = new Administrator();

		admin.sendMail(message);

		message = null;
		user = null;
		to.clear();
		to = null;
		admin = null;

		request.setAttribute("message", "Mail sent to the user");

		Log.log(Log.INFO, "AdministrationAction", "sendMail", "Exited");

		return mapping.findForward("success");
	}
	
	
	
	
	public ActionForward mliQueryOrComplaints(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		  Log.log(5, "AdministrationAction", "displayNpaRegistrationFormList", "Entered");
		
		  AdministrationActionForm adminForm = (AdministrationActionForm)form;
		  
	
		  Administrator queryinfo = new Administrator();
		  

		  
			User creatingUser = getUserInformation(request);
			String createdBy = creatingUser.getUserId();
		  
		  String  depid=queryinfo.getDepartment(createdBy);
		  
		  
		  ArrayList queryDetailsList = queryinfo.getQueryList(depid);
		  
		  	  
					String forward = "";
					if (queryDetailsList == null || queryDetailsList.size() == 0) {
						request.setAttribute("message",
								"No Queries Available For processing");
						forward = "success";
					} else {
						adminForm.setMliQueryList(queryDetailsList);
						forward = "queryList";
					}
				
		  
		  return mapping.findForward(forward);
	  }
	
		
	
	 public ActionForward processQueryStatus(ActionMapping mapping,ActionForm form,
			  HttpServletRequest request,HttpServletResponse response)throws Exception
	  {
		  Log.log(5, "AdministrationAction", "showApprRegistrationFormSubmit", "Entered");
		  HttpSession session = request.getSession(false);
		  
		  AdministrationActionForm queryFrom = (AdministrationActionForm)form;
		  String action = request.getParameter("action");
		//  System.out.println("AA action : "+action);
		  Map qryResponse = queryFrom.getQueryRespon();
		  
		  String[] qryRemarks = queryFrom.getQryRemarks();
		  
		  
			User creatingUser = getUserInformation(request);
			String createdBy = creatingUser.getUserId();
			
			  Administrator queryinfo = new Administrator();
			
		 queryinfo.procesStatus(qryRemarks,qryResponse,createdBy); 
				
		//  System.out.println("AA qryResponse : "+qryResponse);
		 // String checkIdArr[] = apprNpaForm.getCheck();
		  request.setAttribute("message","<b>Query Response has been done successfully :.<b><br>");
		 
		  return mapping.findForward("success");
	  }
	 
	 
	 
	/*	public ActionForward mliQueryBifurcation(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {

			Log.log(Log.INFO, "AdministrationAction", "showChangeHintQA", "Entered");
			  AdministrationActionForm queryFrom = (AdministrationActionForm)form;

			// Get the logged in users old hint question and answer.
			User user = getUserInformation(request);
			Hint hint = user.getHint();
			String oldQuestion = hint.getHintQuestion();
			String oldAnswer = hint.getHintAnswer();

			// Set the old question and answer.
			//dynaActionForm.set("newQuestion", oldQuestion);
			//dynaActionForm.set("newAnswer", oldAnswer);

			user = null;
			hint = null;

			Log.log(Log.INFO, "AdministrationAction", "showChangeHintQA", "Exited");
			return mapping.findForward("success");

		}*/
	 
	
	
}