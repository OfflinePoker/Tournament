<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hatoka="xalan://de.hatoka.common.capi.app.xslt.Lib"
  exclude-result-prefixes="hatoka"
>
  <xsl:import href="de/hatoka/common/capi/app/xslt/ui.xsl" />
  <xsl:param name="localizer" />
  <!-- BEGIN OUTPUT -->
  <xsl:output method="html" encoding="UTF-8" indent="yes" />
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <xsl:call-template name="head">
          <xsl:with-param name="title"><xsl:value-of select="hatoka:getText($localizer, 'title.list.tournament', 'List of:')" /></xsl:with-param>
      </xsl:call-template>
      <body>
	      <div class="col-md-8">
        <form method="POST" action="action">
          <table class="table table-striped">
            <tr>
              <th>Select</th>
              <th>Name</th>
              <th>Date</th>
            </tr>
            <xsl:apply-templates />
          </table>
          <xsl:call-template name="button">
            <xsl:with-param name="name">delete</xsl:with-param>
          </xsl:call-template>
        </form>
        </div>
        <div class="col-md-4">
        <h2>
          <xsl:value-of select="hatoka:getText($localizer, 'info.create.tournament', 'Create new:')" />
        </h2>
        <form method="POST" action="create">
          <xsl:call-template name="input">
            <xsl:with-param name="name">name</xsl:with-param>
            <xsl:with-param name="placeholderKey">placeholder.name.tournament</xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="input">
            <xsl:with-param name="name">buyIn</xsl:with-param>
            <xsl:with-param name="placeholderKey">placeholder.buyin</xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="button">
            <xsl:with-param name="name">create</xsl:with-param>
          </xsl:call-template>
        </form>
        </div>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="tournaments" xmlns="http://www.w3.org/1999/xhtml">
    <tr>
      <td>
        <xsl:call-template name="checkbox">
          <xsl:with-param name="name">tournamentID</xsl:with-param>
          <xsl:with-param name="value"><xsl:value-of select="id" /></xsl:with-param>
        </xsl:call-template>
      </td>
      <td>
      <a>
        <xsl:attribute name="href"><xsl:value-of select="uri" /></xsl:attribute>
        <xsl:value-of select="name" />
      </a>
      </td>
      <td>
        <xsl:call-template name="formatDate">
          <xsl:with-param name="date">
            <xsl:value-of select="date" />
          </xsl:with-param>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>