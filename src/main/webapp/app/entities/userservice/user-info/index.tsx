import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserInfo from './user-info';
import UserInfoDetail from './user-info-detail';
import UserInfoUpdate from './user-info-update';
import UserInfoDeleteDialog from './user-info-delete-dialog';

const UserInfoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserInfo />} />
    <Route path="new" element={<UserInfoUpdate />} />
    <Route path=":id">
      <Route index element={<UserInfoDetail />} />
      <Route path="edit" element={<UserInfoUpdate />} />
      <Route path="delete" element={<UserInfoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserInfoRoutes;
