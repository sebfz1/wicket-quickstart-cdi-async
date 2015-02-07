package quickstart;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.kendo.ui.markup.html.link.BookmarkablePageLink;

public class MyPage extends AbstractBasePage
{
	private static final long serialVersionUID = 1L;

	public MyPage(PageParameters parameters)
	{
		super(parameters);

		// button //
		this.add(new BookmarkablePageLink<Void>("back", HomePage.class));
	}
}
