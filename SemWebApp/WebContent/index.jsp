<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link href="https://fonts.googleapis.com/css?family=Quicksand:300" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Testing Sparql</title>
    <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
    <style>
      body, html {
        margin:0;
        padding: 0;
      }

      .navbar {
      	position: fixed;
      	width: 100%;
      	height: 60px;
      	background-color: #515151;
        border-bottom: #9b9b9b 1px solid;
        z-index: 1;
      }
      #div-logo {
        width: 50px;
      	margin: auto;
        display: block;
        margin-left: auto;
        margin-right: auto;
      }
      #logo-font {
      	font-family: 'Quicksand', sans-serif;
        color:white;
        font-size: 45px;
      }
      .footer {
        width: 100%;
        height: 60px;;
      	background-color: #515151;
        border-top: #9b9b9b 1px solid;
        font-size: 20px !important;
      }
      #footer-font {
      	font-family: 'Quicksand', sans-serif;
        color:white;
        font-size: 20px;
        display: block;
        margin-left: auto;
        margin-right: auto;
      }


      .content {
        position: relative;
        background-color: #ffffff;
        height: 800px;
        width: 80%;
        padding-bottom: 35px;
        margin: auto;
        margin-top:0px;
      }
      .searchFormContainer {
        position: relative;
        margin: auto;
        width: 488px;
        /*background-color: red;*/
        top: 200px;
      }
      #searchSubmit {
        padding: 0;
        border: #515151 2px solid;
        background-color: white;
        width: 80px;
        height: 44px;
        margin-top: 10px;
        margin: auto;
        font-family: 'Quicksand', sans-serif;
        font-size: 20px;
      }
      #searchSubmit:hover {
        background-color: #515151;
        cursor: pointer;
        color: white;

      }
      #searchField {
        background-color: white;
        border: #515151 2px solid;
        width: 400px;
        height: 40px;
        padding: 0;
        font-size: 20px;
        font-family: 'Quicksand', sans-serif;
      }
      #resultsContainer {
        padding-top: 35px;
        margin-top: 35px;
        top: 90px;
        width: 90%;
        min-height: 100%;
        border: 1px solid lightgrey;
        background-color: f7f7f7;
        margin-left: auto;
        margin-right: auto;
      }
      #tableResults {
        border-collapse: collapse;
        width: 100%;
      }
      #tableResults td {
        padding: 10px 10px 10px 15px;
      }
      tr:nth-child(odd) {background-color: lightgrey;}
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

  </head>
  <body>
  	<div class="navbar">
        <div id="div-logo"><span id="logo-font">wo:</span></div>
    </div>
    <div class="content">

      <div class="searchFormContainer">
        <form action="Search" id="searchForm" method="post">
          <input type="text" id="searchField" name="searchField" placeholder="type in your SPARQL query.."/>
          <input type="submit" id="searchSubmit" name="searchSubmit" value="Go!"/>
        </form>
      </div>
    </div>
    <div class="footer">
        <!-- <span id="footer-font">&copy; Vasco &amp; Jolan</span> -->
    </div>
  </body>
</html>
