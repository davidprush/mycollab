/**
 * Copyright Â© MyCollab
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mycollab.module.project.view.ticket;

import com.mycollab.common.i18n.GenericI18Enum;
import com.mycollab.core.utils.DateTimeUtils;
import com.mycollab.core.utils.SortedArrayMap;
import com.mycollab.module.project.domain.ProjectTicket;
import com.mycollab.module.project.ui.components.TicketRowRender;
import com.mycollab.vaadin.AppUI;
import com.mycollab.vaadin.UserUIContext;
import com.vaadin.icons.VaadinIcons;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author MyCollab Ltd
 * @since 5.1.1
 */
public class StartDateOrderComponent extends TicketGroupOrderComponent {
    private SortedArrayMap<Long, DefaultTicketGroupComponent> startDateAvailables = new SortedArrayMap<>();
    private DefaultTicketGroupComponent unspecifiedTasks;

    public StartDateOrderComponent() {
        super();
    }

    public StartDateOrderComponent(Class<? extends TicketRowRender> ticketRowRenderCls) {
        super(ticketRowRenderCls);
    }

    @Override
    public void insertTickets(List<ProjectTicket> tickets) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppUI.getLongDateFormat()).withLocale(UserUIContext.getUserLocale());
        for (ProjectTicket ticket : tickets) {
            if (ticket.getStartDate() != null) {
                LocalDate startDate = ticket.getStartDate();
                LocalDate monDay = DateTimeUtils.getFirstDayOfWeek(startDate);
                String monDayStr = formatter.format(monDay);
                Long time = monDay.toEpochDay();
                if (startDateAvailables.containsKey(time)) {
                    DefaultTicketGroupComponent groupComponent = startDateAvailables.get(time);
                    groupComponent.insertTicketComp(buildRenderer(ticket));
                } else {
                    LocalDate maxValue = DateTimeUtils.getLastDayOfWeek(startDate);
                    String sundayStr = formatter.format(maxValue);
                    String titleValue = VaadinIcons.CALENDAR.getHtml() + " " + String.format("%s - %s", monDayStr, sundayStr);

                    DefaultTicketGroupComponent groupComponent = new DefaultTicketGroupComponent(titleValue);
                    startDateAvailables.put(time, groupComponent);
                    addComponent(groupComponent);
                    groupComponent.insertTicketComp(buildRenderer(ticket));
                }
            } else {
                if (unspecifiedTasks == null) {
                    unspecifiedTasks = new DefaultTicketGroupComponent(VaadinIcons.CALENDAR.getHtml() + " " + UserUIContext.getMessage(GenericI18Enum.OPT_UNDEFINED));
                    addComponent(unspecifiedTasks, 0);
                }
                unspecifiedTasks.insertTicketComp(buildRenderer(ticket));
            }
        }
    }
}