package com.axonivy.connector.adobe.acrobat.sign.connector.service;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.project.Project;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;
import ch.ivyteam.ivy.workflow.start.IWebStartable;

/**
 * Service for working with process starts. Code is from portal's
 * ProcessStartAPI
 */
public class ProcessStartService {
	/**
	 * Find start link from friendly request path
	 *
	 * @param friendlyRequestPath friendly path e.g "Start
	 *                            Processes/UserExampleGuide/userExampleGuide.ivp"
	 * @return start link or empty string
	 */
	public static String findRelativeUrlByProcessStartFriendlyRequestPath(String friendlyRequestPath) {
		IWebStartable processStart = findProcessStartByUserFriendlyRequestPath(friendlyRequestPath);
		return processStart != null ? processStart.getLink().getRelative() : StringUtils.EMPTY;
	}

	private static IWebStartable findProcessStartByUserFriendlyRequestPath(String requestPath) {
		return Sudo.get(() -> {
			var pmv = IWorkflowProcessModelVersion.of(Project.current());
			return pmv.getAllStartables()
					.filter(start -> start.getId().contains(requestPath))
					.findAny()
					.orElse(null);
		});
	}
}