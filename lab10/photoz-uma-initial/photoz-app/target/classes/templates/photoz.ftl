<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
    <head>
        <title>Photoz Page</title>
        <link rel="stylesheet" type="text/css" href="/styles.css"/>
    </head>
    <body>
        <div class="wrapper" id="profile">

            <#if app_error??>
                <div class="content">
                    <span class="error">${app_error}</span>
                </div>
            </#if>

            <div class="menu">
                <#if create_photo_allowed == true>
                    <button name="photoBtn" onclick="location.href = '/app/create-photo'">Add Photo</button>
                </#if>
                <button name="tokenBtn" onclick="location.href = '/app/show-token'">Token</button>
                <button name="rptBtn" onclick="location.href = '/app/show-rpt'">RPT</button>
                <button name="accountBtn" onclick="location.href = '${accountUri}'" type="button">Account</button>
                <button name="logoutBtn" onclick="location.href = '/logout'" type="button">Logout</button>
            </div>

            <div class="content">
                <div id="profile-header" class="header">
                    User profile
                </div>

                <div id="profile-content" class="message">
                    <table cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="label">Username</td>
                            <td><span id="username">${token.preferredUsername}</span></td>
                        </tr>
                        <tr class="even">
                            <td class="label">Email</td>
                            <td><span id="email">${token.email}</span></td>
                        </tr>
                        <tr>
                            <td class="label">Name</td>
                            <td><span id="firstName">${token.givenName} ${token.familyName}</span></td>
                        </tr>
                    </table>
                </div>
            </div>

            <#list photoz?keys as username>
                <div class="users-divider">
                </div>

                <div class="content">
                    <div class="header">
                        ${username}'s photoz
                    </div>

                    <div class="message">
                        <table cellpadding="0" cellspacing="0">
                            <#list photoz[username] as photo>
                                <#if photo?is_even_item>
                                <tr class="even">
                                <#else>
                                <tr>
                                </#if>

                                    <td class="label">${photo.name}</td>
                                    <#if photo.hasViewDetailsScope>
                                        <td class="button"><button onclick="location.href = '/app/details/${photo.id}'">Details</button></td>
                                    <#else>
                                        <td class="button"><button onclick="location.href = '/app/details/${photo.id}'">Ask for details</button></td>
                                    </#if>
                                    <#if photo.hasDeleteScope>
                                        <td class="button"><button onclick="location.href = '/app/delete/${photo.id}'">Delete</button></td>
                                    <#else>
                                        <td class="button"><button onclick="location.href = '/app/delete/${photo.id}'">Ask for delete</button></td>
                                    </#if>
                                    <td></td>
                                </tr>
                            </#list>
                        </table>
                    </div>
                </div>
            </#list>
        </div>

    </body>
</html>