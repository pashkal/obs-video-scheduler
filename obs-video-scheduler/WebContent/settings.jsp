<%@page import="util.Config"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<title>Settings</title>
</head>
<body>
	<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">OBS Video Scheduler settings</div>
				<div class="panel-body">
					<form action="/UpdateConfig" method="post">
						<div class="form-group">
							<label for="obs-host">OBS host</label>
							<br>
			 				<input
								id="obs-host" type="text"
								name="obs-host"
								value=<%out.println(Config.getOBSHost());%> />
						</div>

						<div class="form-group">
							<label for="obs-video-dir">Video directory on OBS host</label>
							<br>
			 				<input
								id="obs-video-dir" type="text"
								name="obs-video-dir"
								value=<%out.println(Config.getOBSVideoDir());%> />
						</div>

						<div class="form-group">
							<label for="server-video-dir">Video directory on the server</label>
							<br>
			 				<input
								id="server-video-dir" type="text"
								name="server-video-dir"
								value=<%out.println(Config.getServerVideoDir());%> />
						</div>
						<button type="submit" class="btn btn-primary">Update</button>
				</form>
				</div>
		</div>
	</div>
</body>
</html>