package com.axonivy.connector.adobe.acrobat.sign.connector.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.workflow.IProcessStart;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;

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
		IProcessStart processStart = findProcessStartByUserFriendlyRequestPath(friendlyRequestPath);
		return processStart != null ? processStart.getLink().getRelative() : StringUtils.EMPTY;
	}

	private static IProcessStart findProcessStartByUserFriendlyRequestPath(String requestPath) {
		return Sudo.get(() -> {
			IProcessStart processStart = getProcessStart(requestPath, Ivy.request().getProcessModelVersion());
			if (processStart != null) {
				return processStart;
			}
			List<IApplication> applicationsInSecurityContext = IApplicationRepository.of(ISecurityContext.current()).all();
			for (IApplication app : applicationsInSecurityContext) {
				IProcessStart findProcessStart = filterPMV(requestPath, app).findFirst().orElse(null);
				if (findProcessStart != null) {
					return findProcessStart;
				}
			}
			return null;
		});
	}

	private static IProcessStart getProcessStart(String requestPath, IProcessModelVersion processModelVersion) {
		return IWorkflowProcessModelVersion.of(processModelVersion)
				.findStartElementByUserFriendlyRequestPath(requestPath);
	}

	private static Stream<IProcessStart> filterPMV(String requestPath, IApplication application) {
		return filterActivePMVOfApp(application).map(p -> getProcessStart(requestPath, p)).filter(Objects::nonNull);
	}

	private static Stream<IProcessModelVersion> filterActivePMVOfApp(IApplication application) {
		return application.getProcessModelsSortedByName().stream().filter(pm -> isActive(pm))
				.map(IProcessModel::getReleasedProcessModelVersion).filter(pmv -> isActive(pmv));
	}

	private static boolean isActive(IProcessModelVersion processModelVersion) {
		return processModelVersion != null && processModelVersion.getActivityState() == ActivityState.ACTIVE;
	}

	private static boolean isActive(IProcessModel processModel) {
		return processModel.getActivityState() == ActivityState.ACTIVE;
	}
}